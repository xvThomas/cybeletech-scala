package com.github.xvthomas.json.impl.purescala

import com.github.xvthomas.json.impl.purescala.JsonParserError.{NumberNotImplemented, UnexpectedToken}

/**
 *  Syntax analyzer for Json string
 */
object JsonParser {

  type Continuation = String
  // scalastyle:off class.type.parameter.name
  final case class NextJsonAST[+JsonAST](jsonAST: JsonAST, continuation: Continuation)
  type Parser[A] = Either[JsonParserError, NextJsonAST[A]]

  /**
   *  Expected a given token
   *  @param expected the token to be checked
   *  @param input A string
   *  @return The continuation of the given input if the expected token is present otherwise an UnexpectedToken error
   */
  private def expectToken(expected: JsonToken, input: Continuation): Either[JsonParserError, Continuation] =
    JsonTokenParser.nextToken(input) match {
      case Left(syntacticError)                      => Left(syntacticError)
      case Right(next) if next.jsonToken != expected => Left(JsonParserError.UnexpectedToken(List(expected), next.jsonToken, input))
      case Right(next)                               => Right(next.continuation)
    }

  final case class CheckToken(isPresent: Boolean, continuation: Continuation)

  /**
   *  Check and pass token if present
   *  @param expected The expected token
   *  @param input A string
   *  @return The continuation of input if the expected token is present otherwise input
   */
  private def checkToken(expected: JsonToken, input: Continuation): CheckToken =
    JsonTokenParser.nextToken(input) match {
      case Left(_)                                   => CheckToken(isPresent = false, input)
      case Right(next) if next.jsonToken != expected => CheckToken(isPresent = false, input)
      case Right(next)                               => CheckToken(isPresent = true, next.continuation)
    }

  /**
   *  Parse value of a JsonObject member
   *  [BNF] value ::= string | array | object | number | boolean | null
   */
  private[purescala] def parseValue(input: Continuation): Parser[JsonValue] = {
    val nextTokenOrError = JsonTokenParser.nextToken(input)
    nextTokenOrError.flatMap(nextToken =>
      nextToken.jsonToken match {
        case JsonToken.StringValue(value) =>
          Right(NextJsonAST(
            value match {
              case "true"  => JsonBoolean(value = true)
              case "false" => JsonBoolean(value = false)
              case "null"  => JsonNull
              case _       => JsonString(value)
            },
            nextToken.continuation
          ))
        case JsonToken.NumberValue(_) => Left(NumberNotImplemented)
        case JsonToken.LeftBrace      => parseObject(input)
        case JsonToken.LeftBracket    => parseArray(input)
        case _ => Left(
            UnexpectedToken(
              expected = List(JsonToken.StringValue(value = ""), JsonToken.NumberValue(value = 0), JsonToken.LeftBrace, JsonToken.LeftBracket),
              found = nextToken.jsonToken,
              input = input
            )
          )
      }
    )
  }

  /**
   *  Parse key of a JsonObject member
   *  [BNF] key ::= string
   */
  private[purescala] def parseKey(input: Continuation): Parser[Key] =
    JsonTokenParser.nextToken(input).flatMap(nextToken =>
      nextToken.jsonToken match {
        case JsonToken.StringValue(value) => Right(NextJsonAST(Key(value), nextToken.continuation))
        case _                            => Left(UnexpectedToken(List(JsonToken.StringValue("")), nextToken.jsonToken, input))
      }
    )

  /**
   *  Parse member of a JsonObject
   *  [BNF] member ::= key ":" value
   */
  private[purescala] def parseMember(input: Continuation): Parser[Member] =
    for {
      nextKey               <- parseKey(input)
      nextCommaContinuation <- expectToken(JsonToken.Colon, nextKey.continuation)
      nextValue             <- parseValue(nextCommaContinuation)
    } yield NextJsonAST(Member(nextKey.jsonAST.string, nextValue.jsonAST), nextValue.continuation)

  /**
   *  Parse members of a JsonObject
   *  [BNF] members ::= member | member "," members
   */
  def parseMembers(input: Continuation, jsonObject: JsonObject): Parser[JsonObject] =
    for {
      nextMember <- parseMember(input)
      checkComma = checkToken(JsonToken.Comma, nextMember.continuation)
      nextMembers <-
        if (checkComma.isPresent) {
          parseMembers(checkComma.continuation, jsonObject.add(nextMember.jsonAST))
        } else {
          Right(NextJsonAST(jsonObject.add(nextMember.jsonAST), nextMember.continuation))
        }
    } yield nextMembers

  /**
   *  Parse a JsonObject
   *  [BNF] object ::= "{" [members] "}"
   */
  private[purescala] def parseObject(input: Continuation): Parser[JsonObject] = {
    for {
      leftBraceContinuation <- expectToken(JsonToken.LeftBrace, input) // "{ ..." => "..."
      emptyJsonObject = JsonObject()
      checkRightBrace = checkToken(JsonToken.RightBrace, leftBraceContinuation) // "} ... " => "..."
      nextJsonObject <-
        if (checkRightBrace.isPresent) {
          Right(NextJsonAST(emptyJsonObject, leftBraceContinuation)) // empty object case
        } else {
          parseMembers(leftBraceContinuation, emptyJsonObject)
        }
      nextRightBraceContinuation <- expectToken(JsonToken.RightBrace, nextJsonObject.continuation)
    } yield NextJsonAST(nextJsonObject.jsonAST, nextRightBraceContinuation)
  }

  /**
   *  Parse elements of a JsonArray
   *  [BNF] elements ::= value | value "," elements
   */
  private[purescala] def parseElements(input: Continuation, jsonArray: JsonArray): Parser[JsonArray] =
    for {
      nextValue <- parseValue(input)
      checkComma = checkToken(JsonToken.Comma, nextValue.continuation)
      nextElements <-
        if (checkComma.isPresent) {
          parseElements(checkComma.continuation, jsonArray.add(nextValue.jsonAST))
        } else {
          Right(NextJsonAST(jsonArray.add(nextValue.jsonAST), nextValue.continuation))
        }
    } yield nextElements

  /**
   *  Parse a JsonArray
   *  [BNF] array ::= "[" [elements] "]"
   */
  private[purescala] def parseArray(input: Continuation): Parser[JsonArray] =
    for {
      leftBracketContinuation <- expectToken(JsonToken.LeftBracket, input) // "[ ..." => "..."
      emptyJsonArray = JsonArray()
      checkRightBracket = checkToken(JsonToken.RightBracket, leftBracketContinuation) // "] ... " => "..."
      nextJsonArray <-
        if (checkRightBracket.isPresent) {
          Right(NextJsonAST(emptyJsonArray, leftBracketContinuation)) // empty array case
        } else {
          parseElements(leftBracketContinuation, emptyJsonArray)
        }
      nextRightBracketContinuation <- expectToken(JsonToken.RightBracket, nextJsonArray.continuation)
    } yield NextJsonAST(nextJsonArray.jsonAST, nextRightBracketContinuation)

  def parse(json: String): Either[JsonParserError, JsonValue] = parseValue(json).map(_.jsonAST)
}
