package util

import java.net.URLEncoder

import scalaj.http.Http

class ArtistInfoGrabber {
  def getRandomSongTitles(artistName: String, numSongs: Int): Seq[String] = {
    //for (page <- getArtistPage(artistName);
    //     works <- s"https://musicbrainz.org/artist/$page/works") yield works
    Nil
  }

  def getArtistPage(artistName: String): Option[String] = {
    val url = "https://musicbrainz.org/taglookup?tag-lookup.artist=" + URLEncoder.encode(artistName, "UTF-8")
    val pageRegex = "<td>\\n +<a href=\"(.+?)\"".r
    val page = pageRegex.findFirstMatchIn(Http(url).asString.body).map(_.group(1))
    page
  }
}
