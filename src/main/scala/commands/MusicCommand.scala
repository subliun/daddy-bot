package commands

import music.MusicHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene
import scala.collection.JavaConverters._

class MusicCommand() extends CommandHandler {
  override val command: Seq[String] = List("music")
  override val helpInfo: String = "Play music"

  val musicHandler = new MusicHandler()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val guild = event.getGuild
    val channels = guild.getVoiceChannels
    args.split(" ").head.trim() match {
      case "play" =>
        val maybeVoiceChannel = Option(event.getMember.getVoiceState.getChannel)
        println(maybeVoiceChannel)

        maybeVoiceChannel match {
          case Some(voiceChannel) =>
            musicHandler.loadAndPlay(channel, guild, voiceChannel, args.split(" ").tail.mkString)
            ""
          case _ =>
            "you're not even in a voice channel. you can't listen to music"
        }

      case "skip" =>
        musicHandler.skipTrack(guild)
        "shit track got skipped"

      case "join" =>
        val maybeVoiceChannel = Option(event.getMember.getVoiceState.getChannel)
        println(maybeVoiceChannel)

        maybeVoiceChannel match {
          case Some(voiceChannel) =>
            musicHandler.join(guild, voiceChannel)
            "Joining the audio channel"
          case _ =>
            "you're not even in a voice channel. you can't listen to music"
        }

      case "leave" =>
        musicHandler.leave(guild)
        "goodbye audio friends"

      case "queue" =>
        musicHandler.listQueue(guild);

      case _ =>
        "I don't know how to do that with music"
    }
  }
}
