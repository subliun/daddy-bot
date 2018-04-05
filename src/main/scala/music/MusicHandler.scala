package music

import java.util.concurrent.TimeUnit

import com.sedmelluq.discord.lavaplayer.player.{AudioLoadResultHandler, DefaultAudioPlayerManager}
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}
import net.dv8tion.jda.core.entities.{Guild, MessageChannel, TextChannel, VoiceChannel}
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.managers.AudioManager

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.concurrent.Future

class MusicHandler extends ListenerAdapter {

  private val googlePlayWrapper = new GooglePlayWrapper()

  private val playerManager = new DefaultAudioPlayerManager()
  AudioSourceManagers.registerRemoteSources(playerManager)
  AudioSourceManagers.registerLocalSource(playerManager)

  private val musicManagers = mutable.Map[String, GuildMusicManager]()
  private var lastActiveChannel: Option[MessageChannel] = None

  def getGuildAudioPlayer(guild: Guild): GuildMusicManager = {
    val id = guild.getId

    val musicManager = musicManagers.get(id) match {
      case Some(manager) =>
        manager
      case _ =>
        val manager = new GuildMusicManager(playerManager, onTrackStart)
        musicManagers += (id -> manager)
        manager
    }

    guild.getAudioManager.setSendingHandler(musicManager.getSendHandler)

    musicManager
  }

  def onTrackStart(track: AudioTrack): Unit = {
    lastActiveChannel.foreach(_.sendMessage("Now playing: "+ track.getInfo.title))
  }

  // this is truly awful
  def loadAndPlay(channel: MessageChannel, guild: Guild, toJoin: VoiceChannel, trackSearch: String): Unit = {
    val musicManager = getGuildAudioPlayer(guild)

    lastActiveChannel = Some(channel)
    if (trackSearch.contains(".")) { // probably a url
      playerManager.loadItemOrdered(musicManager, trackSearch, new AudioLoadResultHandler {
        override def trackLoaded(track: AudioTrack): Unit = {
          play(guild, musicManager, BotTrack(() => Some(track), TrackInfo(track.getInfo.title, track.getInfo.author)), toJoin)
          channel.sendMessage("Loaded track to queue: **" + track.getInfo.title + "**").queue()
        }

        override def playlistLoaded(playlist: AudioPlaylist): Unit = {
          val firstTrack = Option(playlist.getSelectedTrack).getOrElse(playlist.getTracks.get(0))
          for (track <- playlist.getTracks.asScala) {
            // this will end up adding most of them to the queue
            play(guild, musicManager, BotTrack(() => Some(track), TrackInfo(track.getInfo.title, track.getInfo.author)), toJoin)
          }

          channel.sendMessage("Adding to queue **" + firstTrack.getInfo.title + "** (from playlist **" + playlist.getName + "**)").queue()
        }

        override def loadFailed(exception: FriendlyException): Unit = {
          channel.sendMessage("I don't know that song pal").queue()
          exception.printStackTrace()
        }

        override def noMatches(): Unit = {
          channel.sendMessage("I don't know that song pal").queue()
        }
      })
    } else {
      googlePlayWrapper.getSong(trackSearch) match {
        case Some(song) =>
          val track = BotTrack(() => {
            var loadedTrack: Option[AudioTrack] = None
            val future = playerManager.loadItemOrdered(musicManager, googlePlayWrapper.getUrl(song).toString, new AudioLoadResultHandler {
              override def trackLoaded(track: AudioTrack): Unit = {
                loadedTrack = Some(track)
              }

              override def playlistLoaded(playlist: AudioPlaylist): Unit = {}

              override def loadFailed(exception: FriendlyException): Unit = {
                noMatches()
                exception.printStackTrace()
              }

              override def noMatches(): Unit = {
                channel.sendMessage("Error playing song").queue()
              }

            })
            val timeToWait = 10000
            future.get(timeToWait, TimeUnit.SECONDS) // give up on loading the track if it takes more than 10 seconds
            loadedTrack
          }, TrackInfo(song.getTitle, song.getArtist))

          channel.sendMessage("Loaded track to queue: **" + track.toString + "**").queue()
          play(guild, musicManager, track, toJoin)
        case None =>
          channel.sendMessage("I don't know that song pal").queue()
      }
    }
  }

  def play(guild: Guild, musicManager: GuildMusicManager, track: BotTrack, voiceChannel: VoiceChannel): Unit = {
    connectToVoiceChannel(voiceChannel, guild.getAudioManager)

    musicManager.scheduler.queue(track)
  }

  def skipTrack(guild: Guild): Boolean = {
    val wasSkipped = getGuildAudioPlayer(guild).scheduler.nowPlaying.isDefined
    getGuildAudioPlayer(guild).scheduler.nextTrack()
    wasSkipped
  }

  def join(guild: Guild, voiceChannel: VoiceChannel): Unit = {
    connectToVoiceChannel(voiceChannel, guild.getAudioManager)
  }

  def leave(guild: Guild): Unit = {
    guild.getAudioManager.closeAudioConnection()
  }

  def listQueue(guild: Guild): String = {
    val scheduler = getGuildAudioPlayer(guild).scheduler
    val tracks = scheduler.nowPlaying ++ scheduler.queue.asScala.toList
    println(tracks)

    if (tracks.isEmpty) {
      "there's nothing in the queue"
    } else {
      //avoid intellij highlighting issue by using case
      tracks.zipWithIndex.map { case(track: BotTrack, i: Int) => (i + 1) + ". **" + track.toString + "**"}.mkString("\n")
    }
  }

  def connectToVoiceChannel(voiceChannel: VoiceChannel, audioManager: AudioManager): Unit = {
    if (!audioManager.isConnected && !audioManager.isAttemptingToConnect) {
      audioManager.openAudioConnection(voiceChannel)
    }
  }
}
