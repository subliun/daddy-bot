package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class BetListCommand extends CommandHandler {
  override val command: Seq[String] = List("bets", "betlist")
  override val helpInfo: String = "List the active bets at the moment"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = {
    bene.listActiveBets()
  }
}