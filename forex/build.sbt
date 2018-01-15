
name := "forex"
version := "1.0.0"

scalaVersion := "2.12.4"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Ypartial-unification",
  "-language:experimental.macros",
  "-language:implicitConversions"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val versions = new {
  val akka = "2.5.9"
  val akkaHttp = "10.1.0-RC1"
  val circe = "0.8.0"
  val pureconfig = "0.7.2"
  val refined = "0.8.5"
  val eff = "4.6.1"
}

libraryDependencies ++= Seq(
  "com.github.pureconfig"        %% "pureconfig"                      % versions.pureconfig,
  "eu.timepit"                   %% "refined"                         % versions.refined,
  "eu.timepit"                   %% "refined-pureconfig"              % versions.refined,
  "io.swagger"                   % "swagger-jaxrs"                    % "1.5.17",
  "com.github.swagger-akka-http" %% "swagger-akka-http"               % "0.11.2",
  "javax.xml.bind"               % "jaxb-api"                         % "2.3.0",
  "co.pragmati" %% "swagger-ui-akka-http" % "1.1.0",
  "com.typesafe.akka"            %% "akka-actor"                      % versions.akka,
  "com.typesafe.akka"            %% "akka-stream"                     % versions.akka,
  "com.typesafe.akka"            %% "akka-slf4j"                      % versions.akka,
  "com.typesafe.akka"            %% "akka-http"                       % versions.akkaHttp,
  "de.heikoseeberger"            %% "akka-http-circe"                 % "1.18.1",
  "io.circe"                     %% "circe-core"                      % versions.circe,
  "io.circe"                     %% "circe-generic"                   % versions.circe,
  "io.circe"                     %% "circe-generic-extras"            % versions.circe,
  "io.circe"                     %% "circe-java8"                     % versions.circe,
  "io.circe"                     %% "circe-jawn"                      % versions.circe,
  "org.atnos"                    %% "eff"                             % versions.eff,
  "org.atnos"                    %% "eff-monix"                       % versions.eff,
  "org.zalando"                  %% "grafter"                         % "2.3.0",
  "ch.qos.logback"               % "logback-classic"                  % "1.2.3",
  "com.typesafe.scala-logging"   %% "scala-logging"                   % "3.7.2",
  "com.softwaremill.sttp"        %% "core"                            % "1.1.4",
  "com.softwaremill.sttp"        %% "async-http-client-backend-monix" % "1.1.4",
  "com.softwaremill.sttp"        %% "circe"                           % "1.1.4",
  "com.github.blemale"           %% "scaffeine"                       % "2.3.0",
  compilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.5"),
  compilerPlugin("org.scalamacros" %% "paradise"       % "2.1.1" cross CrossVersion.full),
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

dependencyOverrides ++= Seq(
  "org.typelevel" %% "cats-core" % "0.9.0"
)

parallelExecution in Test := false

wartremoverWarnings in (Compile, compile) ++= Warts.allBut(
  Wart.ImplicitParameter,
  Wart.ExplicitImplicitTypes,
  Wart.Nothing,
  Wart.PublicInference,
  Wart.Overloading
)

scalafmtOnCompile in ThisBuild := true