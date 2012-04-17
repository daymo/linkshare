name := "linkshare"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

// using 0.2.4+ of the sbt web plugin
scanDirectories in Compile := Nil

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)

libraryDependencies ++= {
  val liftVersion = "2.4" // Put the current/latest lift version here [https://github.com/lift/framework]
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default")
}
 
libraryDependencies ++= Seq(
	"org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
	//"org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "container", 
    //"org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container",
	"org.specs2" %% "specs2" % "1.8.2" % "test" withSources,
	"org.mockito" % "mockito-all" % "1.9.0" % "test",
	"org.scala-tools.testing" %% "scalacheck" % "1.9" % "test",
	"org.slf4j" % "slf4j-api" % "1.6.4" withSources,
	"ch.qos.logback" % "logback-classic" % "1.0.1",
   "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
   "com.h2database" % "h2" % "1.3.165", // In-process database, useful for development systems
   "org.jsoup" % "jsoup" % "1.6.1",
   "org.scalaz" %% "scalaz-core" % "6.0.4" withSources,
	"commons-io" % "commons-io" % "2.2"
)

seq(webSettings :_*)
