package forsaken.dockerlogreader

import forsaken.dockerlogreader.docker.DockerProcesses
import forsaken.dockerlogreader.log.*
import forsaken.dockerlogreader.log.LogLevel.ERROR
import zio.*
import zio.stream.*
import io.sentry.{Sentry, SentryOptions}
import zio.process.CommandError

import scala.sys.process.ProcessLogger

object App extends ZIOAppDefault with DockerProcesses:

  final val emptyProcessLogger = new ProcessLogger {
    override def out(s: => String): Unit = ()

    override def err(s: => String): Unit = ()

    override def buffer[T](f: => T): T = f
  }

  def run: ZIO[Any, Throwable, Unit] =

    Sentry.init(new SentryOptions {
      setDsn(
        "https://3a649c7bd20bf09fa70e6811605e6308@o4507940499357696.ingest.de.sentry.io/4508121477677136"
      )
    })

    // Replace with your actual container ID or name
    val containerName = "trader_abci_0"

    val logSink: ZSink[Any, Throwable, Log, Nothing, Unit] =
      ZSink.foreach { log =>
        Sentry.captureException(new Exception(log.message))
        Console.printLine("Error logged: " + log.level)
      }

    val logParser = LogParser()

    // Fetch and print the logs
    for
      containerId <- ZIO
        .fromOption(list(containerName).lazyLines(emptyProcessLogger).headOption)
        .orDieWith(_ => new Exception("Container ID not found."))
      _ <- Console.printLine("Getting logs from Container ID: " + containerId)

      _ <- ZStream
        .fromIterable(logs(containerId).lazyLines(emptyProcessLogger))
        .map(logParser.parse) // Parse each log line
        .collectSome
        .filter(_.level == ERROR)
        .foreach { log =>
          Sentry.captureException(new Exception(log.message))
          Console.printLine(s"Error logged: $log")
        }
        .catchAll(err =>
          Console.printLine("Stream failed with error: " + err.getMessage) *>
            ZIO.die(err)
        ) // Die with the error if something goes wrong
    yield ()
