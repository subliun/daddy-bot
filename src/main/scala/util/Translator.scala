package util

import java.net.URLEncoder

import scalaj.http._

class Translator {

  val subscriptionKey: String = ConfigReader.read("translator-subscriptionkey")

  def translate(text: String, toLang: String, fromLang: Option[String] = None): Option[String] = {
    val authToken = getAccessToken(subscriptionKey)
    println(authToken)

    val escapedText = URLEncoder.encode(text, "UTF-8")
    val request =
      Http(s"https://api.microsofttranslator.com/v2/Http.svc/Translate?text=$escapedText&to=$toLang" + fromLang.map(from => s"&from=$from").getOrElse(""))
        .header("Authorization", authToken)

    val regex = ">(.+?)<\\/string>".r
    val result = request.asString

    println(result.body)

    regex.findFirstMatchIn(result.body).flatMap(m => Option(m.group(1)))
  }

  def getAccessToken(key: String): String = {
    val request = Http("https://api.cognitive.microsoft.com/sts/v1.0/issueToken").postData("").header("Ocp-Apim-Subscription-Key", subscriptionKey)
    "Bearer " + request.asString.body
  }
}
