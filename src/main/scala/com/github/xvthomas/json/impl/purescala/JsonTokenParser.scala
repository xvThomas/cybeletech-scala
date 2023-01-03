package com.github.xvthomas.json.impl.purescala

import scala.util.matching.Regex

sealed trait JsonToken

private[purescala] case object JsonToken {
  final case class StringValue(value: String) extends JsonToken

  final case class NumberValue(value: BigDecimal) extends JsonToken

  final case object WhiteSpace extends JsonToken

  final case object LeftBrace extends JsonToken

  final case object RightBrace extends JsonToken

  final case object LeftBracket extends JsonToken

  final case object RightBracket extends JsonToken

  final case object Colon extends JsonToken

  final case object Comma extends JsonToken
}

private[purescala] final case class NextToken(jsonToken: JsonToken, continuation: String)

private[purescala] final case object JsonTokenParser {

  private val whiteSpaceRegex: Regex   = "[ \t\r\n\f]+".r
  private val leftBracketRegex: Regex  = """\[""".r
  private val rightBracketRegex: Regex = """]""".r
  private val leftBraceRegex: Regex    = """\{""".r
  private val rightBraceRegex: Regex   = """}""".r
  private val colonRegex: Regex        = """:""".r
  private val commaRegex: Regex        = """,""".r
  private val stringRegex: Regex       = "\"((?:[^\"\\\\/\b\f\n\r\t]|\\\\u\\d{4})*)\"".r

  private final case class Tokenizer(regex: Regex, tokenize: String => JsonToken)
  private val terminals: List[Tokenizer] = List(
    Tokenizer(whiteSpaceRegex, _ => JsonToken.WhiteSpace),
    Tokenizer(leftBracketRegex, _ => JsonToken.LeftBracket),
    Tokenizer(rightBracketRegex, _ => JsonToken.RightBracket),
    Tokenizer(leftBraceRegex, _ => JsonToken.LeftBrace),
    Tokenizer(rightBraceRegex, _ => JsonToken.RightBrace),
    Tokenizer(colonRegex, _ => JsonToken.Colon),
    Tokenizer(commaRegex, _ => JsonToken.Comma),
    Tokenizer(stringRegex, token => JsonToken.StringValue(value = token.substring(1, token.length - 1)))
    // Missing JsonToken.NumberValue, not yet implemented
  )

  def nextToken(input: String): Either[JsonParserError, NextToken] = {
    if (input.isEmpty) {
      Left(JsonParserError.EoF)
    } else {
      terminals
        .collectFirst { terminal =>
          terminal.regex.findPrefixOf(input) match {
            case Some(token) => NextToken(terminal.tokenize(token), input.substring(token.length))
          }
        }
        .map(next =>
          next.jsonToken match {
            // ignore whiteSpace characters [ \t\n\r\f]
            case JsonToken.WhiteSpace => nextToken(next.continuation)
            case _                    => Right(next)
          }
        ).fold[Either[JsonParserError, NextToken]](Left(JsonParserError.UnrecognizedToken(input)))(identity)
    }
  }
}
