package com.github.xvthomas.json.impl.purescala

/**
 *  Errors produced by json string parsing
 */
sealed trait JsonParserError
object JsonParserError {
  final case class UnexpectedToken(expected: List[JsonToken], found: JsonToken, input: String) extends JsonParserError
  final case class UnrecognizedToken(input: String)                                            extends JsonParserError
  final case object NumberNotImplemented                                                       extends JsonParserError
  final case object EoF                                                                        extends JsonParserError
}
