package util

import scala.util.Try
import scalaj.http.Http

//it's actually quite dumb
class GeniusClient {

  val authToken: String = ConfigReader.read("genius-authtoken")

  def getLyrics(songTitle: String): Option[String] = {
    val request = Http("https://api.genius.com/search").param("q", songTitle).param("access_token", authToken)
    val urlRegex = "\"url\":\"(.+?)\"".r
    val lyricsDivRegex = "<div class=\"lyrics\">([\\s\\S]+?)<\\/div>".r
    val lyricsRegex = "([ \\w,'\"!\\(\\)\\?;\\]\\[:]+)(<br|<\\/a)".r

    for (
      songUrl <- urlRegex.findFirstMatchIn(request.asString.body);
      lyricsHtml <- Try(Http(songUrl.group(1)).asString.body).toOption;
      lyricsBlock <- lyricsDivRegex.findFirstIn(lyricsHtml)
      ) yield lyricsRegex.findAllMatchIn(lyricsBlock).map(_.group(1)).mkString("\n").replace("[", "\n[") // hack for nice verses
  }
}
