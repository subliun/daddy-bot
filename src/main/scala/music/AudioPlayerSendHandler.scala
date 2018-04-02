package music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.core.audio.AudioSendHandler

class AudioPlayerSendHandler(audioPlayer: AudioPlayer) extends AudioSendHandler {
  private var lastFrame: Option[AudioFrame] = None

  override def provide20MsAudio(): Array[Byte] = {
    if (lastFrame.isEmpty) lastFrame = Option(audioPlayer.provide())

    val data: Array[Byte] = lastFrame match {
      case Some(frame) => frame.data
      case _ => null
    }

    data
  }

  override def canProvide: Boolean = {
    lastFrame = Option(audioPlayer.provide())

    lastFrame.isDefined
  }

  override def isOpus: Boolean = {
    true
  }
}
