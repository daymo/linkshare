package com.linkshare.lib

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import com.linkshare.model.Article
import net.liftweb.common.Logger
import java.net.URL
import scala.collection.mutable.{ HashMap }
import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import xml.{Node, XML}
import net.liftweb.mapper.{By, MaxRows, OrderBy, Descending}

case class FetchIt(url: String)
case class ArticleEntry(url: String, title: String, pubDate: String, category:String)
case class RemoveEntry(article: Article)

object HomepageActor extends LiftActor with ListenerManager with Logger {

  private var entries = Vector[Article]()

  def createUpdate = entries take 5

  override def lowPriority = {
    case a: ArticleEntry => try {
      val article = Article.create.title(a.title).link(a.url).category(a.category).pubDate(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(a.pubDate)).saveMe()
      add(article)
      updateListeners()
    }
    case RemoveEntry(article) => remove(article); updateListeners() // I do nothing with this right now
    case l: FetchIt => fetch(l.url)
  }

  private def add(article: Article) = {
    if (!entries.contains(article)) {
      entries +:= article
    }
  }

  private def remove(article: Article) = {
    entries = entries filterNot (_ == article)
  }

  def fetch(url: String): Unit = {
    val rssFeed = XML.load(new URL(url).openConnection.getInputStream)
    val items = rssFeed \\ "channel"
    for {
      item <- items \\ "item"
      if (seen_?(item))
    } HomepageActor ! ArticleEntry( (item \\ "guid").text, (item \\ "title").text, (item \\ "pubDate").text, (item \\ "category").text )
  }

  private def seen_?(item: Node) : Boolean = {
    val url = (item \\ "guid").text
    val seen = Article.find(By(Article.link, url)).isEmpty
    seen
  }
}