package forsaken.dockerlogreader.docker

import zio.process.Command

/** Commands to interact with Docker
  * @author
  *   Petros Siatos
  */
trait DockerCommands:

  /** Fetch the logs for a container
    * @param containerId
    *   The id of the container
    * @return
    */
  def logs(containerId: String): Command =
    Command("docker", "logs", "-f", containerId)

  /** List all container ids with the given name
    * @param containerName
    *   The name of the container
    * @return
    */
  def list(containerName: String): Command =
    Command("docker", "ps", "-aqf", s"name=$containerName")
