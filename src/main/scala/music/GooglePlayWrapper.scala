package music

import java.net.URL

import com.github.felixgail.gplaymusic.api.GPlayMusic
import com.github.felixgail.gplaymusic.model.enums.{ResultType, StreamQuality}
import com.github.felixgail.gplaymusic.model.requests.SearchTypes
import com.github.felixgail.gplaymusic.util.TokenProvider
import util.ConfigReader
import java.net.InetAddress
import java.net.NetworkInterface

import com.github.felixgail.gplaymusic.model.Track

import scala.collection.JavaConverters._
import scala.util.Try

class GooglePlayWrapper {
  private val authToken = TokenProvider.provideToken(
    ConfigReader.read("google-username"),
    ConfigReader.read("google-password").map(_.toInt - 1).map(_.toChar).mkString,
    "50e549c29f4e")
  private val api = new GPlayMusic.Builder().setAuthToken(authToken).setAndroidID("50e549c29f4e").build()

  def getSong(name: String): Option[Track] = {
    val tracks = api.search(name, new SearchTypes(ResultType.TRACK)).getTracks.asScala
    tracks.headOption
  }

  def getUrl(track: Track): URL = {
    track.getStreamURL(StreamQuality.MEDIUM)
  }
}
