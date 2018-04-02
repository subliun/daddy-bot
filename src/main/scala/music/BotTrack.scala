package music

import com.sedmelluq.discord.lavaplayer.player.{AudioLoadResultHandler, AudioPlayerManager}
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}

import scala.concurrent.Future

case class TrackInfo(title: String, artist: String)

case class BotTrack(fetchTrack: () => Option[AudioTrack], info: TrackInfo)