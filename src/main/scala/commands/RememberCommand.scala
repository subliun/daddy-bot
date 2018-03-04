package commands

import java.io.FileWriter

class RememberCommand extends CommandHandler {
  override val command: Seq[String] = List("remember")
  override val helpInfo: String = "Remembers a custom command that can be used later -(when ^[command] is used, the bot will say [text])"
  override val usageText: String = "[command] [text]"

  override def execute(executorName: String, args: String): String = {
    val writer = new FileWriter("memories.txt", true)
    writer.append(args + "\n")
    writer.close()
    s"Remembered ${args.split(" ").headOption.map("^" + _).getOrElse("nothing you dickhead")}"
  }
}
