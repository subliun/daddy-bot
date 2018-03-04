package commands

class AestheticCommand extends CommandHandler {
  override val command: Seq[String] = List("aesthetic")
  override val helpInfo: String = "Makes text a e s t h e t i c"
  override val usageText = s"text"

  override def execute(executorName: String, args: String): String = {
    val aestheticText = args.toCharArray.mkString(" ").replace("  ", " ")
    println("making aesthetic")
    println(aestheticText)
    aestheticText
  }
}
