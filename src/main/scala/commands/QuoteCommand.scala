package commands

import scala.util.Random

class QuoteCommand extends CommandHandler {
  override val command: Seq[String] = List("quote")
  override val helpInfo: String = "Pulls a random quote from Jachin's 2017 quote list or a quote by [name]"
  override val usageText = "(name)"

  override protected def execute(executorName: String, args: String): String = {
    val regex = "(.+) - \"(.+?)\" - ([\\w- ]+)".r
    val quotes = io.Source.fromFile("quotes.txt").mkString
    var matches = regex.findAllMatchIn(quotes).toList

    if (!args.isEmpty) {
      matches = matches.filter(_.group(3).toLowerCase.contains(args.trim().toLowerCase))
    }

    Random.shuffle(matches.map(m => "\"" + m.group(2) + "\"" + " - " + m.group(3))).headOption.getOrElse("No quote found :(")
  }
}
