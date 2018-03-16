package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class BeneCommand extends CommandHandler {
  override val command: Seq[String] = List("bene", "centrelink")
  override val helpInfo: String = "Payout from the dole."

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    bene.bene(event.getAuthor.getId)
  }
}
