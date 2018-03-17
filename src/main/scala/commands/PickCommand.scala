package commands

import scala.util.{Random, Success, Try}

class PickCommand extends CommandHandler {
  override val command: Seq[String] = List("pick")
  override val helpInfo: String = s"Picks randomly from a list of choices \n e.g. \n^${command.head} a b c d -> b \n^${command.head} 2 a b c d -> a d"
  override val usageText: String = "(number to pick) [choices..]"

  val random = new Random()

  override def execute(executorName: String, args: String): String = {
    val options = args.split(" ")
    println(options.length)
    if (options.isEmpty || args == "") {
      "I can't pick from no people, dummy."
    } else {
      val countString = options.head
      if (countString.forall(_.isDigit)) {
        Try(countString.toInt) match {
          case Success(inputCount) =>
            val count = if (inputCount > options.length) options.length else countString.toInt

            val result = (1 to count).zip(Random.shuffle(options.toList.tail)).map(t => s"${t._1}. **${t._2}**").mkString("\n")

            println("calling send message 2")
            if (!result.isEmpty) {
              result
            } else {
              "Picked no one" // dunno when this would actually be called
            }
          case _ =>
            "Oni-chan that number is tooooo big~~~~ ;_;" // I would never write this; it was requested
        }

      } else {
        s"**${options(random.nextInt(options.length))}** has been chosen"
      }
    }
  }
}
