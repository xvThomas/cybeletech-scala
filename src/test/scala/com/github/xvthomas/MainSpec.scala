package com.github.xvthomas

import com.github.xvthomas.Main.{Failure, Success}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{OptionValues, TryValues}

class MainSpec extends AnyFlatSpec with TryValues with OptionValues with should.Matchers {

  "Filter exampleSpec" should "return an empty string" in {
    val file      = getClass.getClassLoader.getResource("data.json").getPath
    val arguments = s"--filter=\"ry\" $file"
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("[")
    res._2 should be(Success)
  }

  "Count exampleSpec" should "succeed and return a json list" in {
    val file      = getClass.getClassLoader.getResource("data.json").getPath
    val arguments = s"--count $file"
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("[")
    res._2 should be(Success)
  }

  "Filter without any animals" should "succeed and return an empty string" in {
    val file      = getClass.getClassLoader.getResource("noAnimals.json").getPath
    val arguments = s"--filter=\"ry\" $file"
    Main.process(arguments.split(" ")) should be("", Success)
  }

  "Count without any animals" should "succeed and return a json list" in {
    val file      = getClass.getClassLoader.getResource("noAnimals.json").getPath
    val arguments = s"--count $file"
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("[")
    res._2 should be(Success)
  }

  "invalid arguments in filter" should "fail with an error message" in {
    val arguments = s"--filter="
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("Error: Unknown or incorrect command")
    res._2 should be(Failure)
  }

  "empty arguments" should "fail with an error message" in {
    val arguments = s""
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("Error: Unknown or incorrect command")
    res._2 should be(Failure)
  }

  "invalid file" should "fail with an error message" in {
    val arguments = "--count missing_file.json"
    val res       = Main.process(arguments.split(" "))
    res._1 should startWith("Error: missing_file.json (No such file or directory)")
    res._2 should be(Failure)
  }

}
