package com.linkshare.comet

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{SHtml, CometListener, CometActor}
import com.linkshare.lib._
import net.liftweb.util.Helpers._
import com.linkshare.model.Article
import net.liftweb.util.ClearClearable
import xml.{Text, Elem, NodeSeq}
import net.liftweb.mapper.{MaxRows, OrderBy, Descending}
import net.liftweb.common.{Box, Empty, Full, Logger}
import net.liftweb.http.js.jquery.JqJsCmds._
import java.text.DateFormat
import org.joda.time.DateTime

class ArticleUpdater extends CometActor with CometListener with Logger {

  private var articles: Vector[Article] = Vector[Article]()
  private var bindLine: NodeSeq = Nil

  def registerWith = HomepageActor

  override def lowPriority = {
    case list: Vector[Article] =>
      val delta = list diff articles
      articles = list
      updateDelta(delta)

  }

  private def hasMoreThan5_? : Boolean = articles.length > 5
  private def getLastArticle = articles.last
  private def lastOne: Box[Article] = if (hasMoreThan5_?) Full(articles.last) else Empty
  //private def removeLast = articles filterNot (lastOne.openOr("") contains _)

  def updateDelta(what: Vector[Article]) {
    partialUpdate(what.foldRight(Noop) {
      case (a: Article, x) => x & PrependHtml("articles", doLine(a)) &
        Hide(a.id.is.toString) & FadeIn(a.id.is.toString, TimeSpan(0),TimeSpan(500))
    })
  }

  def render = "#articles *" #> lines _

  private def lines(xml: NodeSeq): NodeSeq = {
    bindLine = xml
    for {
      m <- articles
      node <- doLine(m)
    } yield node
  }

  private def doLine(m: Article): NodeSeq = {
   ("*" #> addId(bindLine, m.id.is.toString) andThen
     ".category [class+]" #> m.category.is.toLowerCase &
      ".time *" #> hourFormat(m.pubDate.is) &
      ".link *" #> m.title.is &
        ".link [href]" #> "/article/%s".format(m.id.is) &
        ".type *" #> m.category.is)(bindLine)
  }

  private def addId(in: NodeSeq, id: String): NodeSeq = in map {
    case e: Elem => e % ("id" -> id)
    case x => x
  }

}
