package com.github.sassunt.models

import ru.circumflex.orm._

class User extends Record[Long, User] with IdentityGenerator[Long, User] {
  val id = "id".BIGINT.NOT_NULL.AUTO_INCREMENT
  val email = "email".VARCHAR(255).NOT_NULL
  val password = "password".VARCHAR(255).NOT_NULL
  val fullname = "fullname".VARCHAR(255).NOT_NULL
  val isAdmin = "isAdmin".BOOLEAN.NOT_NULL

  def PRIMARY_KEY = id
  def relation = User
}

object User extends User with Table[Long, User] {

  def connect(email: String, password: String): Option[User] = {
    (this AS "q").map{ q => SELECT(q.*).FROM(q).WHERE((q.email EQ email) AND (q.password EQ password)).unique }
  }
}

class Post extends Record[Long, Post] with IdentityGenerator[Long, Post] {
  val id = "id".BIGINT.NOT_NULL.AUTO_INCREMENT
  val title = "title".VARCHAR(255).NOT_NULL
  val content = "content".TEXT.NOT_NULL
  val postedAt = "postedAt".DATE.NOT_NULL
  val author_id = "author_id".BIGINT.NOT_NULL

  def PRIMARY_KEY = id
  def relation = Post
}

object Post extends Post with Table[Long, Post] {
  val fkey = CONSTRAINT("user_id_fkey").FOREIGN_KEY(User, this.author_id -> User.id)

  def allWithAuthor(): Seq[(Post, User)] = {
    val p = Post AS "p"
    val u = User AS "u"
    SELECT(p .* -> u.*).FROM(p JOIN u).WHERE(p.author_id EQ u.id).ORDER_BY(p.postedAt DESC).list.collect{ case (Some(po), Some(us)) => (po, us) }
  }

  def byIdWithAuthorAndComments(id: Long): Option[(Post, User, Seq[Comment])] = {
    val p = Post AS "p"
    val u = User AS "u"
    val c = Comment AS "c"
    val ls = SELECT(p.* AS "post", u.* AS "user", c.* AS "comment").
             FROM((p JOIN u).
             ON("p.author_id = u.id").
             LEFT_JOIN(c).ON("c.post_id = p.id")).
             WHERE(p.id EQ id).list

    (for {
        ((Some(p), Some(u)), xs) <- ls.groupBy{m => (m.get("post"), m.get("user"))}
    } yield (p.asInstanceOf[Post],
             u.asInstanceOf[User],
             xs.flatMap(_.get("comment")).map(_.asInstanceOf[Comment])
    )).headOption
  }
}

class Comment extends Record[Long, Comment] with IdentityGenerator[Long, Comment] {

  def this(post_id: Post, author: String, content: String) = {
   this()
   this.post_id := post_id
   this.author := author
   this.content := content
   this.postedAt := new java.util.Date()
  }
  val id = "id".BIGINT.NOT_NULL.AUTO_INCREMENT
  val author = "author".VARCHAR(255).NOT_NULL
  val content = "content".TEXT.NOT_NULL
  val postedAt = "postedAt".DATE.NOT_NULL
  val post_id = "post_id".BIGINT.NOT_NULL.REFERENCES(Post).ON_DELETE(CASCADE)

  def PRIMARY_KEY = id
  def relation = Comment
}

object Comment extends Comment with Table[Long, Comment] {
  val fkey = CONSTRAINT("post_id_fkey").FOREIGN_KEY(Post, this.post_id -> Post.id)

  def allWithAuthorAndComments(): Seq[(Post, User, Seq[Comment])] = {
    val p = Post AS "p"
    val u = User AS "u"
    val c = Comment AS "c"
    val ls = SELECT(p.* AS "post", u.* AS "user", c.* AS "comment").
             FROM((p JOIN u).ON("p.author_id = u.id").
             LEFT_JOIN(c).ON("c.post_id = p.id")).
             ORDER_BY(p.postedAt DESC).list

    (for {
        ((Some(p), Some(u)), xs) <- ls.groupBy{m => (m.get("post"), m.get("user"))}
    } yield (p.asInstanceOf[Post],
             u.asInstanceOf[User],
             xs.flatMap(_.get("comment")).map(_.asInstanceOf[Comment])
    )).toSeq
  }


}

