package commands

import util.GeniusClient

class LyricCommand extends CommandHandler {
  override val command: Seq[String] = List("lyric")
  override val helpInfo: String = "Gets the lyrics for a song"
  override val usageText = "[song name]"

  val geniusClient = new GeniusClient()
  override protected def execute(executorName: String, args: String): String = {
    if (!args.isEmpty) {
      geniusClient.getLyrics(args).getOrElse("Oh no, I couldn't find that song :(")
    } else {
      "I can't get lyrics for a random song yet ;_;"
    }
  }
}
