package commands

import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

abstract class CommandHandler {
  val command: Seq[String]
  val helpInfo: String
  val usageText: String = ""

  def usage: Option[String] = if (usageText.isEmpty) None else Some(s"${command.head} $usageText")

  def onMessage(event: MessageReceivedEvent, name: String, message: String, channel: MessageChannel): Unit = Unit

  protected def execute(executorName: String, args: String): String
  //TODO I'm not happy about this
  def executeCustom(executorName: String, args: String, channel: MessageChannel): String = execute(executorName, args)
}
