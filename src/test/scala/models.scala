import com.github.sassunt.models._
import ru.circumflex.orm._

import java.util.Date

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

class BasicTests extends Spec with ShouldMatchers with BeforeAndAfterEach {

  override def beforeEach() {
    new DDLUnit(User, Post, Comment).CREATE()
  }

  override def afterEach() {
    new DDLUnit(User, Post, Comment).DROP()
  }

  it ("should create and retriece a User") {
    val user = new User
    user.email := "test@test.com"
    user.password := "secret"
    user.fullname := "Tarou"
    user.isAdmin := false
    user.save()

    val tarou = (User AS "u").map{ u => SELECT(u.*).FROM(u).WHERE(u.email EQ "test@test.com").unique }

    tarou should not be (None)
    tarou.get.fullname() should be ("Tarou")
  }

  it ("should connect a User") {
    val user = new User
    user.email := "test@test.com"
    user.password := "secret"
    user.fullname := "Tarou"
    user.isAdmin := false
    user.save()

    User.connect("test@test.com", "secret") should not be (None)
    User.connect("test@test.com", "badpassword") should be (None)
    User.connect("bad@test.com", "secret") should be (None)
  }

  it ("should create a Post") {
    val user = new User
    user.email := "test@test.com"
    user.password := "secret"
    user.fullname := "Tarou"
    user.isAdmin := false
    user.save()

    val post = new Post
    post.title := "My first post"
    post.content := "Hello!"
    post.postedAt := new Date
    post.author_id := 1
    post.save()

    val postCount = (Post AS "p").map{ p => SELECT(COUNT(p.id)).FROM(p).unique }.get
    postCount should be (1)

    val posts = (Post AS "p").map{ p => SELECT(p.*).FROM(p).WHERE(p.author_id EQ 1).list }
    posts.length should be (1)

    val firstPost = posts.headOption

    firstPost should not be (None)
    firstPost.get.author_id() should be (1)
    firstPost.get.content() should be ("Hello!")
  }

  it ("should retrieve Posts with author") {
    val user = new User
    user.email := "test@test.com"
    user.password := "secret"
    user.fullname := "Tarou"
    user.isAdmin := false
    user.save()

    val post = new Post
    post.title := "My 1st post"
    post.content := "Hello!"
    post.postedAt := new Date
    post.author_id := 1
    post.save()

    val posts = Post.allWithAuthor
    posts.length should be (1)

    val (p, author) = posts.head

    p.title() should be ("My 1st post")
    author.fullname() should be ("Tarou")
  }

  it ("should support Comments") {
    val user = new User
    user.email := "test@test.com"
    user.password := "secret"
    user.fullname := "Tarou"
    user.isAdmin := false
    user.save()

    val post = new Post
    post.title := "My 1st post"
    post.content := "Hello!"
    post.postedAt := new Date
    post.author_id := 1
    post.save()

    new Comment(post, "Suzuki", "Nice post").save()
    //val comment1 = new Comment
    //comment1.author := "Suzuki"
    //comment1.content := "Nice post"
    //comment1.postedAt := new Date
    //comment1.post_id := post
    //comment1.save()

    new Comment(post, "Satou", "I knew that !").save()
    //val comment2 = new Comment
    //comment2.author := "Satou"
    //comment2.content := "I knew that !"
    //comment2.postedAt := new Date
    //comment2.post_id := post
    //comment2.save()

    val userCount = (User AS "u").map{ u => SELECT(COUNT(u.id)).FROM(u).unique }.get
    userCount should be (1)
    val postCount = (Post AS "p").map{ p => SELECT(COUNT(p.id)).FROM(p).unique }.get
    postCount should be (1)
    val commentCount = (Comment AS "c").map{ c => SELECT(COUNT(c.id)).FROM(c).unique }.get
    commentCount should be (2)

    val Some((p, author, comments)) = Post.byIdWithAuthorAndComments(1)

    p.title() should be ("My 1st post")
    author.fullname() should be ("Tarou")
    comments.length should be (2)
    comments(0).author() should be ("Suzuki")
    comments(1).author() should be ("Satou")

  }
}
