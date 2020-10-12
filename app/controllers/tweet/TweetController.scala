package controllers.tweet

import javax.inject.{Inject, Singleton}
import models.Tweet
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent

case class TweetFormData(content: String)

@Singleton
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends  BaseController with I18nSupport {
  val tweets: Seq[Tweet] = (1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}"))
  val form = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )

  // コンパイルエラー回避用に何もしない登録用のstoreメソッドも作成
  def store() = Action { implicit request: Request[AnyContent] =>
    NoContent
  }

  def list() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tweet.list(tweets))
  }

  def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    // tweets.find(_.id.get == id) match { // これじゃだめ？ って思ったけど None == None を避けられるので↓の方が安全なのかも？
    tweets.find(_.id.exists(_ == id)) match {
      case Some(tweet) => Ok(views.html.tweet.show(tweet))
      case None =>   NotFound(views.html.error.page404())
    }
  }

  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tweet.store(form))
  }

}
