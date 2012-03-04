seq(webSettings :_*)

organization := "com.sassunt"

name := "yabe"

scalaVersion := "2.9.1"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "ru.circumflex" % "circumflex-web" % "2.1" % "compile",
  "ru.circumflex" % "circumflex-core" % "2.1" % "compile",
  "ru.circumflex" % "circumflex-ftl" % "2.1" % "compile",
  "ru.circumflex" % "circumflex-orm" % "2.1" % "compile",
  "org.mortbay.jetty" % "jetty" % "6.1.26" % "container",
  "com.h2database" % "h2" % "1.3.164",
  "org.scalatest" %% "scalatest" % "1.7.1" % "test"
)

resolvers ++= Nil
