package commands

import util.Zalgo

import scala.util.Random

class ZalgoCommand extends CommandHandler {
  override val command: Seq[String] = List("zalgo")
  override val helpInfo: String = "H̷͕͂e̥̭̊ ͕̑͘ç̹̭o̐́ͮm̛͋͆e̞̊ͮs̵̱̽"
  override val usageText = "[text]"

  override def execute(executorName: String, args: String): String = {
    Zalgo.makeZalgo(args)
  }
}
