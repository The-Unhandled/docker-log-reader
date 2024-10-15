package forsaken.dockerlogreader.docker

import scala.sys.process.*

/** Commands to interact with Docker
 * @author
 *   Petros Siatos
 */
trait DockerProcesses:

  /** Fetch the logs for a container
   *
   * @param containerId
   * The id of the container
   * @return
   */
  def logs(containerId: String): ProcessBuilder =
    Process(Seq("docker", "logs", "-f", containerId))

  /** List all container ids with the given name
   * @param containerName
   *   The name of the container
   * @return
   */
  def list(containerName: String): ProcessBuilder =
    Process(Seq("docker", "ps", "-aqf", s"name=$containerName"))
