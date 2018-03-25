package commands.bene

import java.util.function.Consumer

import commands.CommandHandler
import net.dv8tion.jda.core.entities.{Message, MessageChannel}
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

class DurryCommand extends CommandHandler {
  override val command: Seq[String] = List("durry")
  override val helpInfo: String = "Buy yourself a durry"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val t = bene.durry(event.getAuthor.getId)

    channel.sendMessage(t._1).queue()
    if (t._2) {
      var durryLength = 14
      val durry = bene.durryString(durryLength)
      channel.sendMessage(durry).queue((message: Message) => {
        for (i <- 1 to durryLength) {
          Thread.sleep(2000)
          message.editMessage(bene.durryString(durryLength - i)).queue()
        }
      })
    }
    ""
  }
}