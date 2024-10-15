package forsaken.dockerlogreader.log

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex

/** @author
  *   Petros Siatos
  */
class LogParser:

  // Define the regex pattern to match the log format
  private val logPattern: Regex =
    raw"\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3})\] \[(INFO|DEBUG|ERROR|WARNING)\] \[(\w+)\] (.+)".r

  def parse(log: String): Option[Log] = log match
    case logPattern(timestampStr, logLevelStr, module, message) =>
      val timestamp = LocalDateTime.parse(
        timestampStr,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS")
      ) // Parse timestamp
      val level = LogLevel.valueOf(logLevelStr) // Parse log level
      Some(Log(timestamp, level, module, message)) // Return a Log object
    case _ => None // If it doesn't match, return None
