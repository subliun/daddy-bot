package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class RaffleCommand extends CommandHandler {
  override val command: Seq[String] = List("raffle")
  override val helpInfo: String = "Buy a raffle ticket for a chance to win big"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    bene.buyRaffleTicket(event.getAuthor.getId, executorName)
  }
}
