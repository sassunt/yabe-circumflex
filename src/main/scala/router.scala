package com.github.sassunt.web

import com.github.sassunt.scalate._
import com.github.sassunt.models._

import ru.circumflex._
import ru.circumflex.core._
import ru.circumflex.web._

class MainRouter extends Router {

    get("/") = {
      val allPosts = Post.allWithAuthorAndComments

      Scalate.layout("/scalate/layouts/index.ssp",
        Map("front" -> allPosts.headOption, "older" -> allPosts.drop(1)))
    }
}
