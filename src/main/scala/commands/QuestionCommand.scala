package commands

import scala.util.Random

class QuestionCommand extends CommandHandler {
  override val command: Seq[String] = List("question", "q")
  override val helpInfo: String = "Answers a question, as long as it is a good question (only yes/no questions allowed)"
  override val usageText = "[question]"

  override def execute(executorName: String, args: String): String = {
    val questionWords = List("do", "did", "does", "am", "is", "are", "has",
      "have", "was", "were", "will", "can",
      "could", "shall", "should", "would")

    val goodQuestion = questionWords.exists(word => args.toLowerCase.startsWith(word))

    if (goodQuestion) {
      val responses = List("Yes.", "No.", "Maybe.", "I cannot say.")
      Random.shuffle(responses).head
    } else {
      "That's not a good question."
    }
  }
}
