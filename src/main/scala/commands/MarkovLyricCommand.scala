package commands

import util.{GeniusClient, Markov}

class MarkovLyricCommand extends CommandHandler {
  override val command: Seq[String] = List("markov")
  override val helpInfo: String = "Generates a random sentence based on a song's lyrics"
  override val usageText = "[song name]"

  val geniusClient = new GeniusClient()

  override protected def execute(executorName: String, args: String): String = {
    if (!args.isEmpty) {
      geniusClient.getLyrics(args)
        .map(lyrics => new Markov(lyrics.split("\n").filterNot(s => s.contains("[") || s.contains("]")).mkString(" ").toLowerCase).genSentence())
        .getOrElse("Oh no, I couldn't find that song :(")
    } else {
      "Give me something to work with!"
    }
  }
}
