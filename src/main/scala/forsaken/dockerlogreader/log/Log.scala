package forsaken.dockerlogreader.log

import cats.Show

import java.time.LocalDateTime

/** @author
  *   Petros Siatos
  */
case class Log(
    timestamp: LocalDateTime,
    level: LogLevel,
    module: String,
    message: String
)

object Log:
  implicit val showLog: Show[Log] = Show.show { log =>
    s"${log.timestamp} - ${log.level} - ${log.message}"
  }

object LogExtensions:
  implicit class LogListOps(logs: List[Log]):
    def filterErrors: List[Log] = logs.filter(_.level == LogLevel.ERROR)

enum LogLevel:
  case INFO extends LogLevel
  case DEBUG extends LogLevel
  case ERROR extends LogLevel
  case WARNING extends LogLevel
