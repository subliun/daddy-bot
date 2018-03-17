import java.io.{File, FileWriter, PrintWriter}

import commands._
import commands.bene._
import net.dv8tion.jda.core.{EmbedBuilder, JDA}
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import util.{Boot, GeniusClient}

import scala.util.{Random, Success, Try}

class Bot extends ListenerAdapter {

  val random = new Random()
  val normalCommands: List[CommandHandler] = List(
    new AestheticCommand(), new BootCommand(), new FlipCommand(),
    new LyricCommand(), new MarkovLyricCommand(), new PickCommand(), new QuestionCommand(),
    new QuoteCommand(), new RememberCommand(), new RollCommand(), new TalkCommand(),
    new ToneCommand(), new TranslateCommand(), new TtsCommand(), new ZalgoCommand(),
    new BalanceCommand(), new BetCommand(), new BeneCommand(), new MugCommand(),
    new TripleDipCommand(), new GiveCommand(), new DurryCommand())

  val commands: List[CommandHandler] = normalCommands :+ new HelpCommand(normalCommands)

  override def onMessageReceived(event: MessageReceivedEvent): Unit = {
    val name = event.getAuthor.getName
    val channel = event.getChannel
    val message = event.getMessage.getContentDisplay

    println(s"Got message: $message")
    if (message.startsWith("^") && !event.getAuthor.isBot) {
      println("got content")
      val command = message.tail.split(" ").headOption.getOrElse("")
      val content = message.split(" ").tail.mkString(" ")

      println(command)
      val result = commands
        .find(c => c.command.contains(command))
        .map(_.executeCustom(name, content, event, channel))
        .getOrElse(defaultCommand(command))

      if (!result.isEmpty) channel.sendMessage(result).queue()
    } else {
      commands.foreach(_.onMessage(event, name, message, channel))
    }
  }

  def defaultCommand(commandString: String): String = {
    val memories = io.Source.fromFile("memories.txt").mkString.split("\n").reverse // reverse so that new memories can be created by simply appending
    val message =
      for (
        memory <- memories.find(_.startsWith(commandString));
        content <- Try(memory.split(" ").filterNot(_.replace(" ", "").isEmpty).tail).toOption.map(_.mkString(" "))) yield content

    message.getOrElse("")
  }
}