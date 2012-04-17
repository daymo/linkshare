package bootstrap.liftweb

import net.liftweb._
import http._
import http.RewriteResponse._
import sitemap.{SiteMap, Menu, Loc}
import util.{ NamedPF }
import net.liftweb._
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.{Props}
import common.{Full}
import com.linkshare.model._
import _root_.net.liftweb.sitemap.Loc._
import com.linkshare.lib.FetchRssTask._
import com.linkshare.lib.FetchRssTask

class Boot {
  def boot {
  
        if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
        			               Props.get("db.url") openOr 
        			               "jdbc:h2:database/lift_proto.db;AUTO_SERVER=TRUE",
        			               Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, Article)
  
    // where to search snippet
    LiftRules.addToPackages("com.linkshare")

    // build sitemap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu
      Menu.i("Articles") / "articles",
      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"),
        "Static Content")))

    LiftRules.setSiteMap(SiteMap(entries:_*))

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
          ParsePath(List("exceptions","404"),"html",false,false))
    })

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
        // What is the function to test if a user is logged in?

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.unloadHooks.append( () => FetchRssTask ! FetchRssTask.Stop )
    FetchRssTask ! DoIt

  }

}