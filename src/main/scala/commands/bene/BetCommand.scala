package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

import scala.util.Try

class BetCommand extends CommandHandler {
  override val command: Seq[String] = List("bet", "pokies")
  override val helpInfo: String =
    """Gamble your money
      |
      |Sub commands:
      |^bet [amount] - roughly 50/50 chance of winning
      |^bet on #[bet id] [amount]
      |^bet create [odds i.e. 1/10] [title]
      |^bet payout #[bet id] [win/loss]
      |^bet delete #[bet id]
    """.stripMargin

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val splitArgs = args.split(" ")
    if (splitArgs.length == 1) {
      bene.bet(event.getAuthor.getId, args).split("\n").foreach(channel.sendMessage(_).queue())
      ""
    } else {
      val output = splitArgs.head match {
        case "on" =>
          for (id <- splitArgs.lift(1); amountString <- splitArgs.lift(2)) yield {
            bene.betComplex(event.getAuthor.getId, id, amountString)
          }

        case "create" =>
          for (odds <- splitArgs.lift(1); title <- Try(splitArgs.drop(2).mkString(" ")).toOption) yield {
            println(odds)
            bene.createBet(event.getAuthor.getId, title, odds)
          }

        case "payout" if event.getAuthor.getId == adminId =>
          for (id <- splitArgs.lift(1); outcome <- splitArgs.lift(2)) yield {
            bene.completeBet(event.getAuthor.getId, id, outcome)
          }

        case "delete" if event.getAuthor.getId == adminId =>
          for (id <- splitArgs.lift(1)) yield {
            bene.deleteBet(event.getAuthor.getId, id)
          }

        case _ =>
          Some("i don't recognise that way of betting from you (you might not have permissions)")
      }

        output.getOrElse("you've failed to bet, haven't you")
    }
  }

  //payout create delete on
}
