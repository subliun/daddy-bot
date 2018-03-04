package commands

import scala.util.Random

class RollCommand extends CommandHandler {
  override val command: Seq[String] = List("roll")
  override val helpInfo: String = "Roll a die"

  private val random = new Random()

  override def execute(executorName: String, args: String): String = {
    println("actually rolling")
    if (args.startsWith("joint")) {
      val jointString =
        """```
          |      #~~
          |     )#(
          |    ( # ) BONG
          |     ___
          |    |   |
          |    |   |
          |    |   |
          |    |   |
          |    |   |
          |___ |   |
          |\  \|   |
          | \  |   |
          | /-------\
          |(_________)
          | \_______/  BONG
          |```
          |
              """.stripMargin
      jointString
    } else {
      val rollValue = random.nextInt(6) + 1
      val face = " " + getDieFace(rollValue).getOrElse("")

      s"$executorName rolled a $rollValue $face"
    }
  }

  private def getDieFace(n: Int): Option[String] = {
    Map(1 -> "⚀", 2 -> "⚁", 3 -> "⚂", 4 -> "⚃", 5 -> "⚄", 6 -> "⚅").get(n)
  }
}
