import sbt._
import Keys._
import play.Project._

object ProjectBuild extends Build {

  playScalaSettings

  val appName         = "CWS"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    jdbc,
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
    //"c3p0" % "c3p0" % "0.9.0.4",
    "org.scalatest" %% "scalatest" % "2.0" % "test",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "org.apache.httpcomponents" % "httpcore" % "4.3.1",
    "org.apache.httpcomponents" % "httpclient" % "4.3.1" ,
    "org.apache.httpcomponents" % "httpmime" % "4.3.2", 
    "com.sksamuel.scrimage" %% "scrimage-core" % "1.4.2",
    "com.sksamuel.scrimage" %% "scrimage-canvas" % "1.4.2",
    "com.sksamuel.scrimage" %% "scrimage-filters" % "1.4.2"
  )

  val main = play.Project(appName, appVersion, appDependencies) settings (
    scalaVersion := "2.10.3") 
}

/*    templatesImport += "controllers.lib._",
    templatesImport += "views.lib._",
    templatesImport += "scala.xml.NodeSeq",
    routesImport += "controllers.lib.Binders._",
    lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" * "*.less"),    
    testOptions in Test := Nil,
    excludeFilter in unmanagedSources := ".#*.scala",
    Keys.fork in Test := false,
    parallelExecution in Test := false,
    //javaOptions ++= Seq("-Xmx2G", "-Xss2M", "-Djavax.net.debug=all"),
    scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8"),
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Test, packageBin) := true,
    publishArtifact in (Test, packageDoc) := false,
    publishArtifact in (Test, packageSrc) := false,
    artifactName := { (config: sbt.ScalaVersion, module: ModuleID, artifact: Artifact) => artifact.classifier match {
      case Some(c) => "%s-%s.%s".format(artifact.name, c, artifact.extension)
      case None => "%s.%s".format(artifact.name, artifact.extension)
    } }
  ) dependsOn(core)

*/
  
