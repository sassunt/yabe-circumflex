package com.github.sassunt.bootstrap

import com.github.sassunt.models._

import ru.circumflex.orm._
import ru.circumflex.web._

import scala.xml.XML

object Web extends StandaloneServer with Bootstrap {

  def main(args: Array[String]): Unit = {
    new DDLUnit(User, Post, Comment).DROP_CREATE
    Deployment.readAll(XML.load(getClass.getResourceAsStream("/init.data.xml"))).foreach(_.process)

    // server start.
    run()

    new DDLUnit(User, Post, Comment).DROP
  }
}

trait Bootstrap {
  self: StandaloneServer =>

  def run() = {
    start()
    println("Embedded server running on port %s. Press any key to stop." format port)
    System.in.read()
    stop()
  }

}
