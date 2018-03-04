package commands

class HelpCommand(commands: Seq[CommandHandler]) extends CommandHandler {
  override val command: Seq[String] = List("help")
  override val helpInfo: String = "I need somebody"
  override val usageText = "(command that you need help with)"

  override protected def execute(executorName: String, args: String): String = {
    if (args.isEmpty) {
      commands.map(printHelp).mkString("\n\n")
    } else {
      commands.find(_.command.exists(_.contains(args.toLowerCase.trim()))).map(printHelp).getOrElse("I don't know anything about that command.")
    }
  }

  def printHelp(command: CommandHandler): String = {
    s"^**${command.command.head}** ${command.usageText} \n${command.helpInfo}"
  }
}
