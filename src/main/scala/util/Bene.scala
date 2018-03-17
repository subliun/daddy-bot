package util

import java.io.FileWriter

import scala.collection.mutable
import scala.util.{Success, Try}

//does not consider multithreading implications which might bite me at some point
//should really use a mutex to lock balance when a command is being executed
/*
  assets:
  security guards that protect you from being mugged
  shares from actual stock market

 */
class Bene {

  val moneyFilePath = "money.txt"

  val startingBalance = 100

  val betChance = 0.4

  val lowestBene = 50
  val highestBene = 300
  val beneCooldown: Int = 60 * 15 // in seconds
  val lastPayTimes: mutable.Map[String, Long] = mutable.Map[String, Long]()

  val jailTime: Int = 60 * 5 // in seconds
  val mugChance = 0.15
  val lastJailTime: mutable.Map[String, Long] = mutable.Map[String, Long]()

  val tripleDipChance = 0.01
  val tripleDipCooldown = 0 //60 * 5 // seconds
  val tripleDipCost = 100
  val tripleDipWinnings = 20000
  val lastTripleDip: mutable.Map[String, Long] = mutable.Map[String, Long]()

  //id is their snowflake id on discord and is guaranteed to be globally unique
  case class UserInfo(id: String, balance: Long)


  def outputTime(timeInMillis: Long): String = {
    val seconds = timeInMillis / 1000
    val minutes = seconds / 60

    if (minutes > 0) {
      s"${Math.ceil(seconds / 60.0).toLong} minutes"
    } else {
      s"${seconds % 60} seconds"
    }
  }

  def bene(id: String): String = {
    if (lastPayTimes.get(id).isEmpty) lastPayTimes(id) = 0

    if (System.currentTimeMillis() - lastPayTimes(id) > beneCooldown * 1000) {
      lastPayTimes(id) = System.currentTimeMillis()
      val info = userInfoCreateIfAbsent(id)
      val payout = Math.floor(lowestBene +  Math.random() * (highestBene - lowestBene)).toLong
      val newBalance = info.balance + payout
      updateBalance(id, newBalance)
      "winz just gave you **$" + payout + "**. u now have **$" + newBalance + "**. congrats"
    } else {
      lastPayTimes.get(id).map(millis => "u gotta wait for " + outputTime(beneCooldown * 1000 - (System.currentTimeMillis() - millis)) + " mate").getOrElse("i fucked up")
    }
  }

  def durry(id: String): (String, Boolean) = {
    val user = userInfoCreateIfAbsent(id)
    val durryCost = 10
    if (user.balance < durryCost) {
      ("u can't afford that mate", false)
    } else {
      updateBalance(id, user.balance - durryCost)
      ("you've bought a durry for **$" + durryCost + "**", true)
    }
  }

  def durryString(length: Int): String = {
    val spaces = " " * length
    val paper = "_" * length

    if (length > 0) {
      s"""
         |```
         |  $spaces )
         | $spaces (
         | _$paper )
         |[_[$paper#
         |```
    """.stripMargin
    } else {
      s"""
         |```
         |
         |
         | _
         |[_[#
         |```
    """.stripMargin
    }

  }

  def give(giverId: String, receiverId: String, receiverName: String, amountString: String): String = {
    parseMoneyString(amountString) match {
      case Success(amount) =>
        if (giverId == receiverId) {
          "good job infinite money"
        } else {
          val giver = userInfoCreateIfAbsent(giverId)
          val receiver = userInfoCreateIfAbsent(receiverId)
          if (amount <= 0) {
            "don't be a cheap cunt"
          } else if (amount > giver.balance) {
            "u dont have enuf money bro"
          } else {
            updateBalance(giverId, giver.balance - amount)
            updateBalance(receiverId, receiver.balance + amount)
            "you gave **$" + amount + "** to " + receiverName
          }
        }
      case _ => "cmon man help a brother out"
    }
  }

  def tripleDip(id: String, name: String): String = {
    if (lastTripleDip.get(id).isEmpty) lastTripleDip(id) = 0

    if (System.currentTimeMillis() - lastTripleDip(id) >= tripleDipCooldown * 1000) {
      lastTripleDip(id) = System.currentTimeMillis()
      val info = userInfoCreateIfAbsent(id)
      if(info.balance >= tripleDipCost) {
        updateBalance(id, info.balance - tripleDipCost)
        if (Math.random() < tripleDipChance) {
          updateBalance(id, info.balance + tripleDipWinnings)
          "WINNER! WINNER! WINNER! " + name + " just won the jackpot! $" + tripleDipWinnings + " has been awarded to them! WINNER! WINNER! WINNER!"
        } else {
          "You bought a ticket for **$" + tripleDipCost + "**. Unfortunately you had no luck winning this time."
        }
      } else {
        "lucky you're poor - the lotto is a scam"
      }
    } else {
      lastTripleDip.get(id).map(millis => "u gotta wait for " + outputTime(tripleDipCooldown * 1000 - (System.currentTimeMillis() - millis)) + " mate").getOrElse("i fucked up")
    }
  }

  def mug(mugerId: String, mugeeId: String, mugeeName: String): String = {
    if (lastJailTime.get(mugerId).isEmpty) lastJailTime(mugerId) = 0

    if (System.currentTimeMillis() - lastJailTime(mugerId) > jailTime * 1000) {
      val muger = userInfoCreateIfAbsent(mugerId)
      val mugee = userInfoCreateIfAbsent(mugeeId)

      if (mugee.balance <= 0) {
        "u can't mug the homeless"
      } else {
        if (Math.random() < mugChance) {
          val amountStolen =  Math.floor(Math.random() * (mugee.balance / 4)).toLong
          updateBalance(mugeeId, mugee.balance - amountStolen)
          updateBalance(mugerId, muger.balance + amountStolen)
          "u managed to steal **$" + amountStolen + "** off " + mugeeName
        } else {
          lastJailTime(mugerId) = System.currentTimeMillis()
          val fine = Math.floor(Math.random() * (muger.balance * 0.02)).toLong
          updateBalance(mugerId, muger.balance - fine)
          "\uD83D\uDEA8\uD83D\uDC6E\uD83D\uDEA8**POLICE**\uD83D\uDEA8\uD83D\uDC6E\uD83D\uDEA8 Its the police! looks like u got caught. thats a **$" + fine + "** fine and five minutes in the big house for you!"
        }
      }
    } else {
      lastJailTime.get(mugerId).map(millis => {
        val timeLeft = jailTime * 1000 - (System.currentTimeMillis() - millis)
        "ur in jail for another " + outputTime(timeLeft) + ". don't drop the soap!"
      }).getOrElse("i fucked up")
    }
  }

  def balance(id: String): Long = {
    userInfoCreateIfAbsent(id).balance
  }

  def bet(id: String, betString: String): String = {
    val info = userInfoCreateIfAbsent(id)

    parseMoneyString(betString) match {
      case Success(amount) =>
        if (info.balance <= 0) {
          "get outta here poor boy"
        } else if (amount > info.balance) {
          "you're too poor for that mate"
        } else if (amount < 1) {
          "u gotta put coins in the machine mate"
        } else {
          if (Math.random() < betChance) {
            updateBalance(id, info.balance + amount)
            "bro you won! wow **$" + amount + "**, that's heaps good! drinks on u ay"
          } else {
            updateBalance(id, info.balance - amount)
            "shit man, you lost **$" + amount + "**. better not let the middy know"
          }
        }
      case _ =>
        "you gotta put coins in the machine mate"
    }
  }

  def parseMoneyString(s: String): Try[Long] = {
    Try(s.replace("$", "").toLong)
  }

  def userInfoCreateIfAbsent(id: String): UserInfo = {
    retrieveUserInfo(id) match {
      case Some(info) =>
        info

      case _ =>
        println(id)
        createUser(id)
        retrieveUserInfo(id).getOrElse(throw new IllegalStateException("Something is wrong with the filesystem"))
    }
  }

  def retrieveUserInfo(id: String): Option[UserInfo] = {
    //reverse so that the newest balance is taken
    readMoneyFile().reverse.find(_.startsWith(id)).map(s => UserInfo(s.split(" ").head, Try(s.split(" ").last.toLong).getOrElse(0)))
  }

  def createUser(id: String): Unit = {
    updateBalance(id, startingBalance)
  }

  def readMoneyFile(): Seq[String] = {
    FileUtil.readLines(moneyFilePath)
  }

  def updateBalance(id: String, newBalance: Long): Unit = {
    val writer = new FileWriter(moneyFilePath, true)
    writer.append(id + " " + (if(newBalance < 0) 0 else newBalance) + "\n")
    writer.close()
  }
}
