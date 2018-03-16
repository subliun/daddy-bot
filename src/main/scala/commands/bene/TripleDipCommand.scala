package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class TripleDipCommand extends CommandHandler {
  override val command: Seq[String] = List("tripledip", "lotto")
  override val helpInfo: String = "Take a small chance at getting a lot of money."

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    bene.tripleDip(event.getAuthor.getId, executorName)
  }
}
