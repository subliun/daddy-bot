name := "DiscordBot"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.jetbrains.kotlin" % "kotlin-stdlib" % "1.2.0",
  "com.squareup.moshi" % "moshi" % "1.5.0",
  "se.michaelthelin.spotify" % "spotify-web-api-java" % "2.0.0",
  "com.github.scribejava" % "scribejava-apis" % "5.0.0")
