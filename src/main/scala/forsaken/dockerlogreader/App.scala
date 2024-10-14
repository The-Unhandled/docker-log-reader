package forsaken.dockerlogreader

import zio._

object App extends ZIOAppDefault with DockerCommands:

  def run: ZIO[Any, Throwable, Unit] =
    // Replace with your actual container ID or name
    val containerName = "trader_abci_0"

    // Fetch and print the logs
    for
      containerId <- list(containerName).string
      _ <- Console.printLine(s"Getting logs from Container ID: $containerId")
      logs <- logs(containerId).linesStream.foreach(Console.printLine(_))
    yield ()
