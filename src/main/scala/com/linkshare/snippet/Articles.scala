package com.linkshare.snippet

import net.liftweb._
import common.{Logger, Box, Empty, Full}
import http._
import mapper._
import mapper.view.MapperPaginatorSnippet
import net.liftweb.util._
import Helpers._
import xml.{Elem, Node, NodeSeq, Text}
import com.linkshare.model.Article
import java.text.DateFormat
import java.text.DateFormat._
import java.util.Locale


class Articles extends DispatchSnippet with Logger {

  override def dispatch = {
    case "all" => all
    case "detail" => detail
    case "paginate" => paginator.paginate _
  }

  private val paginator = new MapperPaginatorSnippet(Article) {
    override def itemsPerPage = 20
    //override def count = Article.count
    //override def page = Article.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
  }

  def all = "li *" #> many(paginator.page)

  lazy val article : Box[Article] = Article.find(
    By(Article.id,S.param("id").map(
      _.toLong).openOr(0L)))

  def detail =  {
    article.map { article =>
      single(article) &
        ".link" #> article.link.is &
        "#date" #> (DateFormat.getDateTimeInstance().format(article.pubDate.is))
    } openOr("*" #> "That article does not exist.")
  }

  private def many(articles: List[Article]) =
    articles.map(a => single(a))

  private def single(article: Article) : CssSel =
    "#title" #> article.title.is &
      "#category" #> article.category.is &
      "a [href]" #> "/article/%s".format(article.id.toString)

  private def single(box: Box[Article]): CssSel =
    single(box.openOr(new Article))

}