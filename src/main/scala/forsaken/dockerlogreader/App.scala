package forsaken.dockerlogreader

import forsaken.dockerlogreader.docker.DockerCommands
import forsaken.dockerlogreader.log.{Log, LogParser}
import zio.*
import zio.stream.*
import cats.implicits.*

object App extends ZIOAppDefault with DockerCommands:

  def run: ZIO[Any, Throwable, Unit] =
    // Replace with your actual container ID or name
    val containerName = "trader_abci_0"

    val logSink: ZSink[Any, Throwable, Log, Nothing, Unit] =
      ZSink.foreach(log => Console.printLine(log.show))

    // Fetch and print the logs
    for
      containerIds <- list(containerName).lines
      containerId <- ZIO
        .fromOption(containerIds.headOption)
        .orElseFail(new Exception("Container ID not found."))
      _ <- Console.printLine(s"Getting logs from Container ID: $containerId")
      logParser = LogParser()
      _ <- logs(containerId).linesStream
        .map(logParser.parse)                                 // Parse each log line
        .map(ZIO.fromOption)                                  // Convert to ZIO
        .flatMap(ZStream.fromZIOOption(_))
        .run(logSink)
        .catchAll(err =>
          ZIO.die(err)
        ) // Die with the error if something goes wrong
      _ <- ZIO.never
    yield ()
