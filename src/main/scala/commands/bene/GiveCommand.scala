package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

import scala.collection.JavaConverters._
import scala.util.Try

class GiveCommand extends CommandHandler {
  override val command: Seq[String] = List("give")
  override val helpInfo: String = "Give someone else money"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val toReceieve = event.getMessage.getMentionedUsers.asScala.toArray
    if (toReceieve.lengthCompare(1) < 0) {
      "don't be a stingy cunt"
    } else if (toReceieve.lengthCompare(1) > 0) {
      "slow down oprah"
    } else {
      bene.give(event.getAuthor.getId, toReceieve.head.getId, toReceieve.head.getName, Try(args.split(" ").last).getOrElse("")) // the give command will handle and empty like an invalid number
    }
  }
}