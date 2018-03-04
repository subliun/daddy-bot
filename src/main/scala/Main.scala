import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import util.ConfigReader

object Main extends App {

  val jda = new JDABuilder(AccountType.BOT).setToken(ConfigReader.read("discord-token")).buildBlocking()

  val bot = new Bot()
  jda.addEventListener(bot)
}