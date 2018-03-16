package commands

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Boot

class BootCommand extends CommandHandler {
  override val command: Seq[String] = List("boot")
  override val helpInfo: String = "Pulls a random boottoobig submission from reddit"

  val boot = new Boot()

  override def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val (title, url) = boot.pullRandomSubmission()
    channel.sendMessage(s"**$title**").queue()
    channel.sendMessage(new EmbedBuilder().setImage(url).build()).queue()
    ""
  }

}
