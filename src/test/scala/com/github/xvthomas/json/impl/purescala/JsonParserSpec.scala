package com.github.xvthomas.json.impl.purescala

import com.github.xvthomas.Fixtures
import com.github.xvthomas.json.impl.purescala.JsonParser.NextJsonAST
import com.github.xvthomas.json.impl.purescala.JsonParserError.{UnexpectedToken, UnrecognizedToken}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.annotation.tailrec

// scalastyle:off multiple.string.literals
// scalastyle:off magic.number
// scalastyle:off named.argument
class JsonParserSpec extends AnyFlatSpec with EitherValues with should.Matchers {

  // scalastyle:off regex
  @tailrec
  final private def parse(input: String): Unit = {
    val res = JsonTokenParser.nextToken(input)
    res match {
      case Left(error) => println(error)
      case Right(value) =>
        println(value.jsonToken)
        parse(value.continuation)
    }
  }

  "specExample" should "be correctly parsed" in {
    // parse(Fixtures.specExample)
    JsonParser.parse(Fixtures.specExample) should be a Symbol("Right")
  }

  "member string" should "be correctly parsed" in {
    val json = """"name" : "John Doe""""
    JsonParser.parseMember(json).value should be(NextJsonAST(Member("name", JsonString("John Doe")), ""))
  }

  "members" should "be correctly parsed" in {
    val json = """"name" : "John Doe", "age" : "nineteen""""
    // parse(json)
    JsonParser.parseMembers(json, JsonObject()).value should be(NextJsonAST(
      JsonObject(Map("name" -> JsonString("John Doe"), "age" -> JsonString("nineteen"))),
      ""
    ))
  }

  "object with one member" should "be correctly parsed" in {
    val json = """{ "name" : "John Doe" }"""
    // parse(json)
    JsonParser.parseObject(json).value should be(NextJsonAST(JsonObject(Map("name" -> JsonString("John Doe"))), ""))
  }

  "object with several members" should "be correctly parsed" in {
    val json = """{ "name" : "John Doe", "retired" : "false", "male" : "true", "maried": "null" }"""
    // parse(json)
    JsonParser.parseObject(json).value should be(NextJsonAST(
      JsonObject(Map("name" -> JsonString("John Doe"), "retired" -> JsonBoolean(false), "male" -> JsonBoolean(true), "maried" -> JsonNull)),
      ""
    ))
  }

  "empty object" should "be correctly parsed" in {
    val json = """{}"""
    // parse(json)
    JsonParser.parseObject(json).value should be(NextJsonAST(JsonObject(Map()), ""))
  }

  "array with one string" should "be correctly parsed" in {
    val json = """[ "John Doe" ]"""
    // parse(json)
    JsonParser.parseArray(json).value should be(NextJsonAST(JsonArray(List(JsonString("John Doe"))), ""))
  }

  "array with several elements" should "be correctly parsed" in {
    val json = """[ "John Doe", "name", "null", "true" ]"""

    // parse(json)
    JsonParser.parseArray(json).value should be(NextJsonAST(
      JsonArray(List(JsonString("John Doe"), JsonString("name"), JsonNull, JsonBoolean(true))),
      ""
    ))
  }

  "non double-quote terminated string" should "produce an UnrecognizedToken error" in {
    val json = """"hello"""
    JsonParser.parseValue(json).left.value should be(UnrecognizedToken(""""hello"""))
  }

  "non lexical terminal" should "produce an UnrecognizedToken error" in {
    val json = """"  % """
    JsonParser.parseValue(json).left.value should be(UnrecognizedToken(""""  % """))
  }

  "malformed object (without comma and value)" should "produce an UnexpectedToken error" in {
    val json = """{ "name" }"""
    JsonParser.parseValue(json).left.value should be(UnexpectedToken(List(JsonToken.Colon), JsonToken.RightBrace, " }"))
  }

  "malformed member key in object " should "produce an UnexpectedToken error" in {
    val json = """{ [name] : "bad" }"""
    JsonParser.parseValue(json).left.value should be(UnexpectedToken(List(JsonToken.StringValue("")), JsonToken.LeftBracket, """ [name] : "bad" }"""))
  }

  """malformed object (starting with "}" instead of "{")""" should "produce an UnexpectedToken error" in {
    val json = """ } """
    JsonParser.parseValue(json).left.value should be(UnexpectedToken(
      List(JsonToken.StringValue(""), JsonToken.NumberValue(0), JsonToken.LeftBrace, JsonToken.LeftBracket),
      JsonToken.RightBrace,
      " } "
    ))
  }

  "empty array" should "be correctly parsed" in {
    val json = """[]"""
    // parse(json)
    JsonParser.parseArray(json).value should be(NextJsonAST(JsonArray(List()), ""))
  }
}
