package commands

import net.dv8tion.jda.core.entities.MessageChannel

class TtsCommand extends CommandHandler {
  override val command: Seq[String] = List("tts")
  override val helpInfo: String = "Say [something] aloud"
  override val usageText: String = "[something]"

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, channel: MessageChannel): String = {
    channel.sendMessage(args).tts(true).queue()
    ""
  }
}
