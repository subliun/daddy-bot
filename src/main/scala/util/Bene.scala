package util

import java.io.FileWriter

import scala.collection.mutable
import scala.util.{Random, Success, Try}

//does not consider multithreading implications which might bite me at some point
//should really use a mutex to lock balance when a command is being executed
/*
  assets:
  bets need to add to the existing bet
  should be a way to get info on who's bet what on a bet
  should be a way to lock bets, so no one can continue to bet

  horse racing
  security guards that protect you from being mugged
  shares from actual stock market

 */
class Bene {

  val moneyFilePath = "money.txt"
  val betsFilePath = "bets.txt"
  val raffleFilePath = "raffle.txt"

  val startingBalance = 100

  val betChance = 0.48

  val lowestBene = 50
  val highestBene = 300
  val beneCooldown: Int = 60 * 15 // in seconds
  val lastPayTimes: mutable.Map[String, Long] = mutable.Map[String, Long]()

  val jailTime: Int = 60 * 5 // in seconds
  val mugChance = 0.18
  val lastJailTime: mutable.Map[String, Long] = mutable.Map[String, Long]()

  val tripleDipChance = 0.01
  val tripleDipCooldown = 8 //1 // seconds
  val tripleDipCost = 100
  val tripleDipWinnings = 20000
  val lastTripleDip: mutable.Map[String, Long] = mutable.Map[String, Long]()

  val raffleCost = 100
  val rafflePayoutThreashold = 10000
  val daddyId = "410801919507038228"

  //id is their snowflake id on discord and is guaranteed to be globally unique
  type UserId = String
  case class UserInfo(id: UserId, balance: Long)

  def readRaffleParticipants(): Seq[(UserId, String)] = {
    FileUtil.readLines(raffleFilePath).map(s => (s.split(" ").head, s.split(" ").tail.mkString))
  }

  def buyRaffleTicket(id: UserId, name: String): String = {
    val info = userInfoCreateIfAbsent(id)

    if (info.balance < raffleCost) {
      "ur too poor for the raffle mate"
    } else if(readRaffleParticipants().map(_._1).contains(info.id)) {
      "you've already bought a ticket and you can't buy two"
    } else {
      FileUtil.append(raffleFilePath, id + " " + name + "\n")
      updateBalance(id, info.balance - raffleCost)
      "you've bought a raffle ticket for **" + formatMoney(raffleCost) + "**. good luck mate"
    }
  }

  def payoutRaffle(): String = {
    Random.shuffle(readRaffleParticipants()).headOption match {
      case Some(t) =>
        val winnerInfo = userInfoCreateIfAbsent(t._1)
        val daddyBotInfo = userInfoCreateIfAbsent(daddyId)

        val payout = daddyBotInfo.balance / 4
        updateBalance(winnerInfo.id, winnerInfo.balance + payout)
        updateBalance(daddyBotInfo.id, 0L)

        FileUtil.write(raffleFilePath, "")
        t._2 + " has won the raffle! **" + formatMoney(payout) + "** has been deposited into their account"
      case _ =>
        "the raffle got payed out but nobody entered ;_;"
    }
  }

  case class Bet(active: Boolean, id: Int, title: String, odds: Double, punters: Map[String, Long]) { //map from id to amount bet
    def toSaveFormat: String = active + " " + id + " " + odds + " " + punters.toString + " " + title

    def toPrintFormat: String = "#" + id + " - \"" + title + "\" - Chances: " + "%.2f".format(odds)
  }

  object Bet {
    def fromSaveFormat(s: String): Option[Bet] = {
      println("attempting to read from save format")
      val split = s.split(" ")
      val mapRegex = "Map\\((.*?)\\) (.+)".r
      val mapContentRegex = "(\\d+?) -> (\\d+)".r

      for (active <- split.headOption.map(_.startsWith("t"));
           id <- split.lift(1);
           odds <- split.lift(2);
           matches <- mapRegex.findFirstMatchIn(s);
           puntersMap <- Option(matches.group(1));
           title <- Option(matches.group(2))) yield {
        val mapContent = mapContentRegex.findAllMatchIn(puntersMap).toList
        val punters = mapContent.map(c => c.group(1)).zip(mapContent.map(c => c.group(2).toLong))

        println("Punters: " + punters)

        val bet = Bet(active, id.toInt, title, odds.toDouble, punters.toMap)
        println(bet)
        bet
      }
    }
  }

  // for synchronization
  class BettingHouse() {
    def genUniqueBetId(): Int = {
      Try(readBets().map(_.id).max).map(_ + 1).getOrElse(1) //starting id is 1
    }

    def saveBet(bet: Bet): Unit = {
      FileUtil.append(betsFilePath, bet.toSaveFormat + "\n")
    }

    def findActiveBet(id: Int): Option[Bet] = {
      readActiveBets().find(_.id == id)
    }

    def readActiveBets(): Seq[Bet] = {
      readBets().filter(_.active)
    }

    def readBets(): Seq[Bet] = {
      println("reading bets")
      FileUtil.readLines(betsFilePath).flatMap(Bet.fromSaveFormat).reverse
    }

    def readBetFile(): String = {
      FileUtil.readFile(betsFilePath)
    }

    def writeBetFile(s: String): Unit = {
      FileUtil.write(betsFilePath, s)
    }

  }

  val bettingHouse = new BettingHouse()

  def createBet(creatorId: UserId, title: String, oddsString: String): String = {
    bettingHouse.synchronized {
      val splitOddsString = oddsString.split("/")
      val maybeOdds = splitOddsString.headOption.flatMap(i => Try(i.toDouble / splitOddsString.last.toDouble).toOption)

      println(maybeOdds)

      if (title.trim().isEmpty) {
        "you gotta bet on something happening"
      } else {
        maybeOdds match {
          case Some(odds) =>
            if (odds == 0.0) {
              "no impossible bets please"
            } else if (odds == 1.0) {
              "if there's no risk there ain't no point playing"
            } else {
              val id = bettingHouse.genUniqueBetId()
              bettingHouse.saveBet(Bet(active = true, id, title, odds, Map[String, Long]()))

              "New bet registered with id #" + id + " with odds " + oddsString + " that \"" + title + "\"" +
                s"\nTo place a bet on this happening say ^bet on #$id [amount]"
            }
          case _ =>
            "fuck off those aren't good odds"
        }
      }
    }
  }

  def betComplex(betterId: UserId, betIdString: String, amountString: String): String = {
    bettingHouse.synchronized {
      val info = userInfoCreateIfAbsent(betterId)

      parseMoneyString(amountString) match {
        case Success(amount) =>
          parseBetIdString(betIdString).flatMap(bettingHouse.findActiveBet) match {
            case Some(bet) =>
              if (info.balance <= 0) {
                "get outta here poor boy"
              } else if (amount > info.balance) {
                "you're too poor for that mate"
              } else if (amount < 1) {
                "u gotta bet money mate"
              } else {
                // appending works because we reverse the bet list upon reading it
                val addedBet = betterId -> (bet.punters.getOrElse(betterId, 0L) + amount)
                bettingHouse.saveBet(bet.copy(punters = bet.punters + addedBet))

                updateBalance(info.id, info.balance - amount)

                "you've bet **" + formatMoney(amount) + "** that " + bet.title + ". good luck bro!"
              }

            case _ =>
              "you gotta bet on a real bet mate. check ur numbers"
          }
        case _ =>
          "u gotta bet money mate"
      }
    }
  }

  private def parseBetIdString(betIdString: String): Option[Int] = {
    Try(betIdString.replace("#", "").toInt).toOption
  }

  private def formatMoney(amount: Long): String = {
    "$" + amount.toString.zipWithIndex.map(t => {
      val i = amount.toString.length - (t._2 + 1)
      if (i % 3 == 0 && i != 0) t._1 + "," else t._1
    }).mkString
  }

  def completeBet(id: UserId, betIdString: String, outcomeString: String): String = {
    bettingHouse.synchronized {
      if (outcomeString.contains("w") || outcomeString.contains("l")) {
        val outcome = outcomeString.contains("w")

        parseBetIdString(betIdString).flatMap(bettingHouse.findActiveBet) match {
          case Some(bet) =>
            bet.punters.foreach(punter => {
              val user = userInfoCreateIfAbsent(punter._1)
              if (outcome) {
                updateBalance(punter._1, user.balance + (punter._2 * (1 / bet.odds)).toLong)
              }
            })

            deleteBet(id, bet.id.toString)

            if (outcome) {
              "congrats to all the gamblers on bet #" + bet.id + ". your winnings have been deposited to your accounts!"
            } else {
              "oh no you've all lost bet #" + bet.id
            }
          case _ =>
            "that bet doesn't exist"
        }
      } else {
        "you've either gotta win or lose, stop fucking up the formatting dingus"
      }
    }
  }

  def listActiveBets(): String = {
    bettingHouse.synchronized {
      val activeBets = bettingHouse.readActiveBets().groupBy(_.id).map(_._2.head).toList //only take unique ids
        if (activeBets.nonEmpty) {
          "**Active Bets in Casino Daddy:**\n" + activeBets.map(_.toPrintFormat).mkString("\n")
        } else {
          "there are no active bets ;_;"
        }
    }
  }

  def deleteBet(id: UserId, betIdString: String): String = {
    bettingHouse.synchronized {
      parseBetIdString(betIdString).flatMap(bettingHouse.findActiveBet) match {
        case Some(bet) =>
          bettingHouse.writeBetFile(bettingHouse.readBetFile().replaceAll("true " + bet.id, "false " + bet.id))
          "bet #" + bet.id + " has been deleted"
        case _ =>
          "u can't delete that which never was, silly"
      }
    }
  }


  def outputTime(timeInMillis: Long): String = {
    val seconds = timeInMillis / 1000
    val minutes = seconds / 60

    if (minutes > 0) {
      s"${Math.ceil(seconds / 60.0).toLong} minutes"
    } else {
      s"${seconds % 60} seconds"
    }
  }

  def bene(id: UserId): String = {
    if (lastPayTimes.get(id).isEmpty) lastPayTimes(id) = 0

    if (System.currentTimeMillis() - lastPayTimes(id) > beneCooldown * 1000) {
      lastPayTimes(id) = System.currentTimeMillis()
      val info = userInfoCreateIfAbsent(id)
      val payout = Math.floor(lowestBene +  Math.random() * (highestBene - lowestBene)).toLong
      val newBalance = info.balance + payout
      updateBalance(id, newBalance)
      "winz just gave you **" + formatMoney(payout) + "**. u now have **" + formatMoney(newBalance) + "**. congrats"
    } else {
      lastPayTimes.get(id).map(millis => "u gotta wait for " + outputTime(beneCooldown * 1000 - (System.currentTimeMillis() - millis)) + " mate").getOrElse("i fucked up")
    }
  }

  def durry(id: UserId): (String, Boolean) = {
    val user = userInfoCreateIfAbsent(id)
    val durryCost = 10
    if (user.balance < durryCost) {
      ("u can't afford that mate", false)
    } else {
      updateBalance(id, user.balance - durryCost)
      ("you've bought a durry for **" + formatMoney(durryCost) + "**", true)
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

  def give(giverId: UserId, receiverId: UserId, receiverName: String, amountString: String): String = {
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
            "you gave **" + formatMoney(amount) + "** to " + receiverName
          }
        }
      case _ => "cmon man help a brother out"
    }
  }

  def tripleDip(id: UserId, name: String): String = {
    if (lastTripleDip.get(id).isEmpty) lastTripleDip(id) = 0

    if (System.currentTimeMillis() - lastTripleDip(id) >= tripleDipCooldown * 1000) {
      lastTripleDip(id) = System.currentTimeMillis()
      val info = userInfoCreateIfAbsent(id)
      if(info.balance >= tripleDipCost) {
        updateBalance(id, info.balance - tripleDipCost)
        if (Math.random() < tripleDipChance) {
          updateBalance(id, info.balance + tripleDipWinnings)
          "WINNER! WINNER! WINNER! " + name + " just won the jackpot! " + formatMoney(tripleDipWinnings) + " has been awarded to them! WINNER! WINNER! WINNER!"
        } else {
          "You bought a ticket for **" + formatMoney(tripleDipCost) + "**. Unfortunately you had no luck winning this time."
        }
      } else {
        "lucky you're poor - the lotto is a scam"
      }
    } else {
      lastTripleDip.get(id).map(millis => "bugger off and go to bed").getOrElse("oh no")//"u gotta wait for " + outputTime(tripleDipCooldown * 1000 - (System.currentTimeMillis() - millis)) + " mate").getOrElse("i fucked up")
    }
  }

  def invadeBahamas(mugerId: UserId, isIdValid: (String) => Boolean): String = {
    val (validAccounts, offshoreAccounts) = retrieveAllUserInfo().partition(info => isIdValid(info.id))

    var totalTaken: Long = 0
    for (account <- offshoreAccounts) {
      val amountTaken = account.balance
      totalTaken += amountTaken
      updateBalance(account.id, account.balance - amountTaken)
    }

    if (totalTaken > 0) {
      for (account <- validAccounts) {
        updateBalance(account.id, account.balance + (totalTaken / validAccounts.size))
      }

      "congratulations comrades. **" + formatMoney(totalTaken) + "** has been seized from " + offshoreAccounts.size + " offshore account(s) in the " +
        "bahamas and redistributed to the working class. <3 communism"
    } else {
      "there is no money hiding in the bahamas"
    }
  }

  def mug(mugerId: UserId, mugeeId: UserId, mugeeName: String): String = {
    if (lastJailTime.get(mugerId).isEmpty) lastJailTime(mugerId) = 0

    if (System.currentTimeMillis() - lastJailTime(mugerId) > jailTime * 1000) {
      val muger = userInfoCreateIfAbsent(mugerId)
      val mugee = userInfoCreateIfAbsent(mugeeId)

      if (mugee.balance <= 0) {
        "u can't mug the homeless"
      } else if(mugee.id == muger.id) {
        "now what would mugging yourself accomplish"
      } else {
        if (Math.random() < mugChance) {
          val amountStolen =  Math.floor(Math.random() * (mugee.balance / 4)).toLong
          updateBalance(mugeeId, mugee.balance - amountStolen)
          updateBalance(mugerId, muger.balance + amountStolen)
          "u managed to steal **" + formatMoney(amountStolen) + "** off " + mugeeName
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

  def balance(id: UserId): Long = {
    userInfoCreateIfAbsent(id).balance
  }

  def bet(id: UserId, betString: String): String = {
    val info = userInfoCreateIfAbsent(id)
    val daddyBotInfo = userInfoCreateIfAbsent(daddyId)

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

            "bro you won! wow **" + formatMoney(amount) + "**, that's heaps good! drinks on u ay"
          } else {
            updateBalance(id, info.balance - amount)
            updateBalance(daddyBotInfo.id, daddyBotInfo.balance + amount)

            val raffleResult =
              if (daddyBotInfo.balance + amount >= rafflePayoutThreashold) {
                payoutRaffle()
              } else ""

            "shit man, you lost **" + formatMoney(amount) + "**. better not let the middy know" + "\n" + raffleResult
          }
        }
      case _ =>
        "you gotta put coins in the machine mate"
    }
  }

  def parseMoneyString(s: String): Try[Long] = {
    Try(s.replace("$", "").toLong)
  }

  def userInfoCreateIfAbsent(id: UserId): UserInfo = {
    retrieveUserInfo(id) match {
      case Some(info) =>
        info

      case _ =>
        println(id)
        createUser(id)
        retrieveUserInfo(id).getOrElse(throw new IllegalStateException("Something is wrong with the filesystem"))
    }
  }

  def retrieveUserInfo(id: UserId): Option[UserInfo] = {
    //reverse so that the newest balance is taken
    readMoneyFile().reverse.find(_.startsWith(id)).map(s => UserInfo(s.split(" ").head, Try(s.split(" ").last.toLong).getOrElse(0)))
  }

  def retrieveAllUserInfo(): Seq[UserInfo] = {
    val ids = readMoneyFile().reverse.map(_.split(" ").head).distinct
    ids.map(retrieveUserInfo).map(_.get)
  }

  def createUser(id: UserId): Unit = {
    updateBalance(id, startingBalance)
  }

  def readMoneyFile(): Seq[String] = {
    FileUtil.readLines(moneyFilePath)
  }

  def updateBalance(id: UserId, newBalance: Long): Unit = {
    FileUtil.append(moneyFilePath, id + " " + (if(newBalance < 0) 0 else newBalance) + "\n")
  }
}
