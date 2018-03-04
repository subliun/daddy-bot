package commands

import scala.util.Random

class FlipCommand extends CommandHandler {
  override val command: Seq[String] = List("flip")
  override val helpInfo: String = "Flip a coin"

  val random = new Random()
  override def execute(executorName: String, args: String): String = {
    s"$executorName's coin landed on ${if (random.nextBoolean()) "heads" else "tails"}."
  }
}
