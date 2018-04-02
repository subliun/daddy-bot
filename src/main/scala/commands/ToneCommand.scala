package commands

import util.FileUtil

import scala.util.Random
import scala.io._

class ToneCommand extends CommandHandler {
  override val command: Seq[String] = List("tone")
  override val helpInfo: String = "Quote tone abet"

  override def execute(executorName: String, args: String): String = {
    val toneQuotes = FileUtil.readLines("tone.txt")
    Random.shuffle(toneQuotes.toList).head
  }
}
