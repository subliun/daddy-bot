package util

object ConfigReader {
  val properties: Seq[String] = FileUtil.readLines("config.properties")

  def read(name: String): String = {
    properties.find(_.startsWith(name)).map(_.split(" ").tail.mkString).getOrElse(throw new IllegalArgumentException("Property not found: " + name))
  }
}
