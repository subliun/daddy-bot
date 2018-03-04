package commands

import util.Translator

import scala.util.Try

class TranslateCommand extends CommandHandler {
  override val command: Seq[String] = List("translate", "tr")
  override val helpInfo: String = s"Translate text from one language into another \n (e.g. ^${command.head} fr Hello -> Bonjour)"
  override val usageText = "[destination lang code] [text]"

  val translator = new Translator()

  override def execute(executorName: String, args: String): String = {
    val content = args.split(" ")

    val maybeTranslatedText =
      for (
        to <- content.headOption;
        text <- Try(content.tail).toOption;
        translation <- translator.translate(text.mkString(" "), to)
      ) yield translation

    println(maybeTranslatedText)
    maybeTranslatedText.getOrElse("Error translating text.")
  }
}
