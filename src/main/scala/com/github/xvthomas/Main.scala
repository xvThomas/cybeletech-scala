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

    (for {
      execution <- analyzeExecution(args)
      json      <- readJsonFile(execution.file).toEither.left.map(_.getMessage)
    } yield (execution.command, json)) match {
      case Left(error) =>
        println(s"Error: $error")
        showHelp()
      case Right((command, json)) =>
        println(execute(command, json))
    }
  }

  def execute(command: Command, json: String): String = {
    val jsonOps: JsonOps = /* new PureScalaJsonOpsImpl() */ new SprayJsonOpsImpl()
    jsonOps.parse(json).fold(
      throwable => s"Error: ${throwable.getMessage}",
      root =>
        jsonOps.prettyPrint(command match {
          case Filter(pattern) => ProcessOps.filter(pattern, root)
          case Count           => ProcessOps.count(root)
        })
    )
  }

  def analyzeExecution(args: Array[String]): Either[String, Execution] =
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

  def readJsonFile(file: Option[File]): Try[String] = Try {
    val source = file match {
      case Some(file) => Source.fromFile(file.path, "UTF-8")
      case None       => Source.fromResource("data.json")
    }
    val res = source.getLines.mkString
    source.close()
    res
  }

  def showHelp(): Unit = {
    println("Usage: run [filter | count] file")
    println()
    println("filter command:")
    println("  --filter=\"<pattern>\"   filter json file using a pattern")
    println("count command:")
    println("  --count                count animals")
    println()
    println("examples:")
    println("""  $ sbt "run --filter=\"ry\" ./doc/data.json"""")
    println("""  $ sbt "run --count ./doc/data.json"""")
  }
}
