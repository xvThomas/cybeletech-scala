package com.github.xvthomas.json.impl.purescala

sealed trait JsonAST
final case class Member(string: String, value: JsonValue) extends JsonAST
final case class Key(string: String)                      extends JsonAST

sealed trait JsonValue                         extends JsonAST
final case class JsonString(value: String)     extends JsonValue
final case class JsonNumber(value: BigDecimal) extends JsonValue
final case class JsonBoolean(value: Boolean)   extends JsonValue
case object JsonNull                           extends JsonValue

final case class JsonArray(value: List[JsonValue]) extends JsonValue {
  def add(jsonValue: JsonValue): JsonArray = copy(value = value ::: List(jsonValue))
}
case object JsonArray {
  def apply(): JsonArray = JsonArray(List.empty)
}

final case class JsonObject(value: Map[String, JsonValue]) extends JsonValue {
  def add(member: Member): JsonObject = copy(value = value + (member.string -> member.value))
}
case object JsonObject {
  def apply(): JsonObject = JsonObject(Map.empty[String, JsonValue])
}
