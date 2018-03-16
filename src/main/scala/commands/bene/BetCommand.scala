package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class BetCommand extends CommandHandler {
  override val command: Seq[String] = List("bet", "pokies")
  override val helpInfo: String = "Gets your current bank balance"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    bene.bet(event.getAuthor.getId, args)
  }
}
