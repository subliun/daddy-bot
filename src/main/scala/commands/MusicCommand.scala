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

  var enabled = true

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val guild = event.getGuild
    val channels = guild.getVoiceChannels
    args.split(" ").head.trim() match {
      case "enable" if event.getAuthor.getId == adminId =>
        enabled = true
        "music enabled (yay)"

      case "disable" if event.getAuthor.getId == adminId =>
        enabled = false
        "music disabled (boo)"

      case s if enabled || event.getAuthor.getId == adminId =>
        s match {
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
            val wasSkipped = musicHandler.skipTrack(guild)

            if (wasSkipped) {
              "shit track got skipped"
            } else {
              "there's nothing even playing how do you expect me to skip nothing"
            }

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

      case _ =>
        "nous sommes dans un annee sans son"
    }
  }
}
