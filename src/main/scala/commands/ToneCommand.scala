package commands

import scala.util.Random

class ToneCommand extends CommandHandler {
  override val command: Seq[String] = List("tone")
  override val helpInfo: String = "Quote tone abet"

  override def execute(executorName: String, args: String): String = {
    val toneQuotes = io.Source.fromFile("tone.txt").mkString.split("\n")
    Random.shuffle(toneQuotes.toList).head
  }
}
