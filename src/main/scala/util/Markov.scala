package util

import rita.{RiMarkov, RiTa}

import scala.util.Random

class Markov(data: String) {

  val markov = new RiMarkov(2)
  markov.loadText(data)

  def genLogicalSentence(): String = {
    val maxAttempts = 10
    var result = ""
    for (_ <- 0 until maxAttempts) {
      result = genSentence()
      println()
      val lastWord = result.split(" ").reverse.find(_.forall(_.isLetterOrDigit)).getOrElse("")
      if (RiTa.isNoun(lastWord) || RiTa.isVerb(lastWord)) {
        return result
      } else {
        println("Rejected " + result)
      }
    }

    result
  }

  def genSentence(): String = {
    val result = markov.generateTokens(Random.nextInt(10) + 5).mkString(" ").dropWhile(!_.isLetterOrDigit)
    result.toArray.headOption.getOrElse(' ').toUpper + result.toArray.tail.mkString.replace(".", "").replace("  ", " ") + "."
  }

  def feed(s: String): Unit = markov.loadText(s)
}
