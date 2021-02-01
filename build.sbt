organization in ThisBuild := "org.mitre"
version in ThisBuild := "1.0-SNAPSHOT"

resolvers += Resolver.mavenLocal
resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .enablePlugins(OsDetectorPlugin)
  .settings(
    name := "jfairseq",
    crossPaths := false,
    autoScalaLibrary := false,
  
    Compile / packageBin / artifact := {
        val prev = (Compile / packageBin / artifact).value
        prev.withClassifier(Some(osDetectorClassifier.value))
    },

    publishMavenStyle := true,
    libraryDependencies ++= Seq(
      "com.github.levyfan" % "sentencepiece" % "0.0.2" classifier osDetectorClassifier.value,
      "junit" % "junit" % "4.12" % Test,
      "org.pytorch" % "pytorch_java_only" % "1.5.0",
      "org.mitre" % "jfastbpe" % "1.0-SNAPSHOT"
    )
  )
 
