package com.linkshare.model

import net.liftweb.mapper._
import net.liftweb.mapper.ManyToMany

class Article extends LongKeyedMapper[Article] with CreatedUpdated with IdPK with ManyToMany {
  def getSingleton = Article

  object title extends MappedString(this,150)

  object wordCount extends MappedInt(this)

  object pubDate extends MappedDateTime(this)

  object category extends MappedString(this,100)

  object link extends MappedString(this,100)

}

object Article extends Article with LongKeyedMetaMapper[Article] {

}
