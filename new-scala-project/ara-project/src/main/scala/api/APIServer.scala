package api

import cats.effect._
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import scala.concurrent.ExecutionContext.global

object APIServer extends IOApp {
  //implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  implicit val loggerFactory = Slf4jFactory.create[IO]
  //implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]

  
  def run(args: List[String]): IO[ExitCode] = {
    // start HTTP servor
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")  
      .withHttpApp(Routes.queryRoutes.orNotFound)  
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}