organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://nightlies.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2-20130822",
  "io.spray"            %   "spray-routing" % "1.2-20130822",
  "io.spray"            %   "spray-testkit" % "1.2-20130822" % "test",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.1",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.1" % "test",
  "org.specs2"          %%  "specs2"        % "1.14" % "test",
  "org.mongodb"         %%  "casbah"        % "2.6.2"
)

seq(Revolver.settings: _*)
