package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene
import scala.collection.JavaConverters._

class BalanceCommand extends CommandHandler {
  override val command: Seq[String] = List("balance", "money", "wallet", "bank", "bal")
  override val helpInfo: String = "Gets your current bank balance"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    if (args.isEmpty) {
      "You currently have **$" + bene.balance(event.getAuthor.getId) + "** in the bnz"
    } else {
      val users = event.getMessage.getMentionedUsers
      if (users.size <= 0) {
        "sorry bro they're with kiwibank"
      } else {
        users.asScala.map(user =>
          s"${user.getName}" + " currently has **$" + bene.balance(user.getId) + "** in the bnz").mkString("\n")
      }
    }
  }
}
