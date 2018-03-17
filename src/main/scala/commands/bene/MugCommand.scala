package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene
import scala.collection.JavaConverters._

class MugCommand extends CommandHandler {
  override val command: Seq[String] = List("mug")
  override val helpInfo: String = "Steal money from another user."
  override val usageText: String = "@[user to mug]"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val toMug = event.getMessage.getMentionedUsers.asScala.toArray
    if (toMug.lengthCompare(1) < 0) {
      "you can't mug no one dickhead"
    } else if (toMug.lengthCompare(1) > 0) {
      "one muggery at a time please"
    } else {
      bene.mug(event.getAuthor.getId, toMug.head.getId, toMug.head.getName)
    }
  }
}
