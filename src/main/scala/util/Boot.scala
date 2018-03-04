package util

import net.dean.jraw.RedditClient
import net.dean.jraw.http.{OkHttpNetworkAdapter, UserAgent}
import net.dean.jraw.oauth.{Credentials, OAuthHelper}

class Boot {
  val credentials: Credentials = Credentials.script(
    ConfigReader.read("reddit-username"),
    ConfigReader.read("reddit-password").map(_.toInt - 1).map(_.toChar).mkString,
    ConfigReader.read("reddit-clientid"),
    ConfigReader.read("reddit-clientsecret"))

  val userAgent = new UserAgent("daddy-bot", "subliun.me", "1.0.0", ConfigReader.read("reddit-username"))
  val reddit: RedditClient = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), credentials)

  def pullRandomSubmission(): (String, String) = {
    val tries = 5
    var last = ("", "")

    for (_ <- 0 to tries) {
      try {
        val randomSubmission = reddit.subreddit("boottoobig").randomSubmission().getSubject
        val title = randomSubmission.getTitle
        val url = randomSubmission.getUrl

        last = (title, url)
        if (randomSubmission.getScore > 10) return last
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }

    last
  }
}
