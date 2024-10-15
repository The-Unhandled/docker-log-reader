package forsaken.dockerlogreader

import forsaken.dockerlogreader.docker.DockerCommands
import forsaken.dockerlogreader.log.*
import forsaken.dockerlogreader.log.LogLevel.ERROR
import zio.*
import zio.stream.*
import io.sentry.{Sentry, SentryOptions}
import zio.process.CommandError

object App extends ZIOAppDefault with DockerCommands:

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

    // Fetch and print the logs
    for
      containerIds <- list(containerName).lines
      containerId <- ZIO
        .fromOption(containerIds.headOption)
        .orDieWith(_ => new Exception("Container ID not found."))
      _ <- Console.printLine("Getting logs from Container ID: " + containerId)

      logParser = LogParser()

      logStream = logs(containerId).linesStream
        .tap(log => Console.printLine("Got Line: " + log)) // Print each log line
        .map(logParser.parse) // Parse each log line
        .collectSome

      // Filter the logs to only show lines containing "ERROR"
      _ <- logStream
          .filter(_.level == ERROR)
          .foreach{ log =>
            Sentry.captureException(new Exception(log.message))
            Console.printLine("Error logged: " + log.level)
          }
          .catchAll(err =>
            Console.printLine("Stream failed with error: " + err.getMessage) *>
              ZIO.die(err)
          ) // Die with the error if something goes wrong
    yield ()
