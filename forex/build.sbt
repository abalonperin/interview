import scala.languageFeature.experimental.macros

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
  val cats = "0.9.0"
  val akka = "2.5.9"
  val akkaHttp = "10.1.0-RC1"
  val akkaHttpCirce = "1.18.1"
  val circe = "0.8.0"
  val pureconfig = "0.7.2"
  val refined = "0.8.5"
  val eff = "4.6.1"
  val grafter = "2.3.0"
  val sttp = "1.1.4"
  val logback = "1.2.3"
  val scalaLogging = "3.7.2"
  val scaffeine = "2.3.0"
  val swaggerAkkaHttp = "0.11.2"
  val swaggerUiAkkaHttp = "1.1.0"
  val swaggerJaxrs = "1.5.17"
  val jaxbApi = "2.3.0"
  val scalatest = "3.0.4"
}

addCompilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.5")
addCompilerPlugin("org.scalamacros" %% "paradise"       % "2.1.1" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.github.pureconfig"        %% "pureconfig"                      % versions.pureconfig,
  "eu.timepit"                   %% "refined"                         % versions.refined,
  "eu.timepit"                   %% "refined-pureconfig"              % versions.refined,
  "io.swagger"                   % "swagger-jaxrs"                    % versions.swaggerJaxrs,
  "com.github.swagger-akka-http" %% "swagger-akka-http"               % versions.swaggerAkkaHttp,
  "javax.xml.bind"               % "jaxb-api"                         % versions.jaxbApi,
  "co.pragmati"                  %% "swagger-ui-akka-http"            % versions.swaggerUiAkkaHttp,
  "com.typesafe.akka"            %% "akka-actor"                      % versions.akka,
  "com.typesafe.akka"            %% "akka-stream"                     % versions.akka,
  "com.typesafe.akka"            %% "akka-slf4j"                      % versions.akka,
  "com.typesafe.akka"            %% "akka-http"                       % versions.akkaHttp,
  "de.heikoseeberger"            %% "akka-http-circe"                 % versions.akkaHttpCirce,
  "io.circe"                     %% "circe-core"                      % versions.circe,
  "io.circe"                     %% "circe-generic"                   % versions.circe,
  "io.circe"                     %% "circe-generic-extras"            % versions.circe,
  "io.circe"                     %% "circe-java8"                     % versions.circe,
  "io.circe"                     %% "circe-jawn"                      % versions.circe,
  "org.atnos"                    %% "eff"                             % versions.eff,
  "org.atnos"                    %% "eff-monix"                       % versions.eff,
  "org.zalando"                  %% "grafter"                         % versions.grafter,
  "ch.qos.logback"               % "logback-classic"                  % versions.logback,
  "com.typesafe.scala-logging"   %% "scala-logging"                   % versions.scalaLogging,
  "com.softwaremill.sttp"        %% "core"                            % versions.sttp,
  "com.softwaremill.sttp"        %% "async-http-client-backend-monix" % versions.sttp,
  "com.softwaremill.sttp"        %% "circe"                           % versions.sttp,
  "com.github.blemale"           %% "scaffeine"                       % versions.scaffeine,
  "org.scalatest"                %% "scalatest"                       % versions.scalatest % "test"
)

dependencyOverrides ++= Seq(
  "org.typelevel" %% "cats-core" % versions.cats
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