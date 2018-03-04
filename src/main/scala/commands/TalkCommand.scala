package commands

import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import util.{ConfigReader, FileUtil, Markov}

class TalkCommand extends CommandHandler {
  override val command: Seq[String] = List("talk")
  override val helpInfo: String = "Make the bot talk (poorly)"

  var markov: Markov = new Markov(FileUtil.readFile(ConfigReader.read("talk-sourcefile")).toLowerCase)

  override def onMessage(event: MessageReceivedEvent, name: String, message: String, channel: MessageChannel): Unit = {
    if (!event.getAuthor.isBot) markov.feed(message)
  }

  override def execute(executorName: String, args: String): String = {
    markov.genSentence()
  }
}
