package util

import java.io.FileWriter

//assume utf-8 encoding
object FileUtil {

  def readFile(path: String): String = {
    val source = io.Source.fromFile(path)
    try source.mkString finally source.close()
  }

  def readLines(path: String): Seq[String] = {
    val source = io.Source.fromFile(path)
    try source.getLines.toList finally source.close()
  }

  def write(path: String, s: String): Unit = {
    val writer = new FileWriter(path, false)
    writer.write(s)
    writer.close()
  }

  def append(path: String, s: String): Unit = {
    val writer = new FileWriter(path, true)
    writer.append(s)
    writer.close()
  }
}
