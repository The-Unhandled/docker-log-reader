package forsaken.dockerlogreader.log

import forsaken.dockerlogreader.log.{LogParser, Log, LogLevel}
import zio.test._
import zio.test.Assertion._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogParserSpec extends ZIOSpecDefault:

  val logParser = LogParser()

  def spec: Spec[Any, Nothing] = suite("LogParserSpec")(
    test("parseLog should correctly parse a valid log entry") {
      val logParser = LogParser()
      val logEntry = "[2024-10-14 14:02:54,898] [INFO] [agent] Sharing Tendermint config on start-up?: False"
      val expectedTimestamp = LocalDateTime.parse("2024-10-14 14:02:54,898", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"))
      val expected = Log(expectedTimestamp, LogLevel.INFO, "agent", "Sharing Tendermint config on start-up?: False")
      assertTrue(logParser.parse(logEntry).get == expected)
    },
    test("parseLog should return None for an invalid log entry") {
      val logEntry = "Invalid log entry"
      assert(logParser.parse(logEntry))(isNone)
    }
  )