package com.github.sassunt

import ru.circumflex._
import ru.circumflex.core._
import ru.circumflex.freemarker._
import ru.circumflex.web._

class MyRouter extends Router {
    get("/") = "<h1>it works!</h1>"

    get("/freemarker") = {
      object simpleObject {
        val name = "Joe"
        val subobj = new Object {
          val name = "Smith"
        }
        override def toString = name + " " + subobj.name
      }
      'obj:= simpleObject
      ftl("index.ftl")
    }
}
