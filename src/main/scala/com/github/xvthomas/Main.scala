package com.github.xvthomas

import com.github.xvthomas.json.JsonOps
import com.github.xvthomas.json.impl.purescala.PureScalaJsonOpsImpl
import com.github.xvthomas.json.impl.spray.SprayJsonOpsImpl

import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

sealed trait Command
final case class Filter(pattern: String) extends Command
case object Count                        extends Command
final case class File(path: String)

case object CommandLineParser {
  val filterRegexp: Regex = """--filter="(.+)"""".r
  val countRegexp: Regex  = "--count".r
  val fileRegexp: Regex   = "(.+)".r

  def parseCommand(arg: String): Option[Command] = arg match {
    case filterRegexp(filter) => Some(Filter(filter))
    case countRegexp()        => Some(Count)
    case _                    => None
  }

  def parseFilePath(arg: String): Option[File] = arg match {
    case fileRegexp(file) => Some(File(file))
    case _                => None
  }
}

final case class Execution(command: Command, file: Option[File] = None)

// scalastyle:off regex
object Main {

  def main(args: Array[String]): Unit = {
    val res = process(args)
    println(process(args)._1)
    sys.exit(res._2)
  }

  val Success = 0
  val Failure = 1

  private[xvthomas] def process(args: Array[String]): (String, Int) = {
    (for {
      execution <- analyzeExecution(args)
      json      <- readJsonFile(execution.file).toEither.left.map(_.getMessage)
    } yield (execution.command, json)) match {
      case Left(error)            => (s"Error: $error\n${showHelp()}", Failure)
      case Right((command, json)) => execute(command, json)
    }
  }

  private def execute(command: Command, json: String): (String, Int) = {
    val jsonOps: JsonOps = new PureScalaJsonOpsImpl() /*new SprayJsonOpsImpl() */
    jsonOps.parse(json).fold(
      throwable => (s"Error: ${throwable.getMessage}", Failure),
      root =>
        (
          jsonOps.prettyPrint(command match {
            case Filter(pattern) => ProcessOps.filter(pattern, root)
            case Count           => ProcessOps.count(root)
          }),
          Success
        )
    )
  }

  private def analyzeExecution(args: Array[String]): Either[String, Execution] =
    args.length match {
      case 0 => Left("Not enough arguments")
      case 1 => CommandLineParser.parseCommand(args(i = 0))
          .fold[Either[String, Execution]](Left("Unknown or incorrect command"))(command => Right(Execution(command)))
      case 2 => CommandLineParser.parseCommand(args(i = 0))
          .fold[Either[String, Execution]](Left("Unknown or incorrect command"))(command =>
            Right(Execution(command, CommandLineParser.parseFilePath(args(i = 1))))
          )
      case _ => Left("Too much arguments")
    }

  private def readJsonFile(file: Option[File]): Try[String] = Try {
    val source = file match {
      case Some(file) => Source.fromFile(file.path, "UTF-8")
      case None       => Source.fromResource("data.json")
    }
    val res = source.getLines.mkString
    source.close()
    res
  }

  private def showHelp(): String =
    "Usage: run [filter | count] file\n\n" +
      "Filter command:\n" +
      "  --filter=\"<pattern>\"   filter json file using a pattern\n" +
      "count command:\n" +
      "  --count                count animals\n\n" +
      "Examples:\n" +
      """  $ sbt "run --filter=\"ry\" ./doc/data.json"""" + "\n" +
      """  $ sbt "run --count ./doc/data.json""""
}
