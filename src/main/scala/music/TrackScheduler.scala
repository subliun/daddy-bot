package music

import java.util.concurrent.LinkedBlockingQueue

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.{AudioTrack, AudioTrackEndReason}

class TrackScheduler(player: AudioPlayer) extends AudioEventAdapter {
  val queue = new LinkedBlockingQueue[BotTrack]()
  var nowPlaying: Option[BotTrack] = None

  def queue(track: BotTrack): Unit = {
    try {
      println("trying to start a track")
      if (player.startTrack(track.fetchTrack().orNull, true)) {
        nowPlaying = Some(track)
      } else {
        println("we put it in the queue")
        queue.offer(track)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def nextTrack(): Boolean = {
    val empty = queue.isEmpty
    val nextTrack = Option(queue.poll())
    nowPlaying = nextTrack
    player.startTrack(nextTrack.flatMap(_.fetchTrack()).orNull, false)

    empty
  }

  override def onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason): Unit = {
    if (endReason.mayStartNext) nextTrack()
  }
}
