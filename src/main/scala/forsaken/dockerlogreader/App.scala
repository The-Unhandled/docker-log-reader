package forsaken.dockerlogreader

import cats.implicits.*
import forsaken.dockerlogreader.docker.DockerProcesses
import forsaken.dockerlogreader.log._
import zio.*
import zio.stream.*

object App extends ZIOAppDefault with DockerProcesses:

  def run: ZIO[Any, Throwable, Unit] =
    // Replace with your actual container ID or name
    val containerName = "trader_abci_0"

    val logSink: ZSink[Any, Throwable, Log, Nothing, Unit] =
      ZSink.foreach(log => Console.printLine(log.show))

    val containerIds = list(containerName).lazyLines

    // Fetch and print the logs
    for
      containerId <- ZIO
        .fromOption(containerIds.headOption)
        .orElseFail(new Exception("Container ID not found."))
      _ <- Console.printLine("Getting logs from Container ID: " + containerId)

      logParser = LogParser()
      _ <- ZStream
        .fromIterable(logs(containerId).lazyLines)
        .map(logParser.parse) // Parse each log line
        .map(ZIO.fromOption) // Convert to ZIO
        .flatMap(ZStream.fromZIOOption(_))
        .run(logSink)
        .catchAll(err =>
          ZIO.die(err)
        ) // Die with the error if something goes wrong
      _ <- ZIO.never // Keep the app running
    yield ()
