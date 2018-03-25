package commands.bene

import commands.CommandHandler
import net.dv8tion.jda.core.entities.{Message, MessageChannel}
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.Bene

import scala.util.Try

class Top5Command extends CommandHandler {
  override val command: Seq[String] = List("top5")
  override val helpInfo: String = "Forbes rich list"

  val bene = new Bene()

  override protected def execute(executorName: String, args: String): String = ""

  override def executeCustom(executorName: String, args: String, event: MessageReceivedEvent, channel: MessageChannel): String = {
    val title = "$$$ **Forbes Rich List** $$$\n"
    val defaultName = "The Bahamas"

    title +
     bene
     .retrieveAllUserInfo()
     .sortBy(_.balance)
     .reverse
     .take(5)
     .zipWithIndex.map(t => {
       s"${t._2 + 1}. ${Try(event.getJDA.getUserById(t._1.id).getName).getOrElse(defaultName)} - **" + "$" + t._1.balance + "**"
     })
     .mkString("\n")
  }
}