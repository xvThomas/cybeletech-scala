package com.github.xvthomas.json.impl.purescala

/**
 *  JsonValue to String printer and pretty printer
 */
object JsonPrinter {
  private def print(jsonValue: JsonValue, space: Int): String = {

    def printMember(key: String, value: JsonValue, indent: Int) = s"""${spaces(indent)}"$key": ${printValue(value, indent)}"""

    def inc(indent: Int): Int       = indent + space
    def newline: String             = if (space > 0) "\n" else " "
    def spaces(indent: Int): String = if (space > 0) " " * indent else ""

    def printValue(jValue: JsonValue, indent: Int): String = jValue match {
      case JsonString(value)    => s""""$value""""
      case JsonNumber(value)    => s"$value"
      case JsonBoolean(boolean) => if (boolean) "true" else "false"
      case JsonNull             => "null"
      case JsonArray(value)     => s"[$newline${value.map(printValue(_, inc(indent))).mkString(s",$newline")}$newline${spaces(indent)}]"
      case JsonObject(value) =>
        s"${spaces(indent)}{$newline${value.map(member => printMember(member._1, member._2, inc(indent))).mkString(s",$newline")}$newline${spaces(indent)}}"
    }

    printValue(jsonValue, indent = 0)
  }

  /**
   *  Pretty printer
   *  @param jsonValue a JsonValue
   *  @param space number of white space in indentation
   *  @return a json string formatted
   */
  def prettyPrint(jsonValue: JsonValue, space: Int = 2): String = print(jsonValue, space)

  /**
   *  Printer
   *  @param jsonValue a JsonValue
   *  @return a json string formatted
   */
  def print(jsonValue: JsonValue): String = print(jsonValue, space = 0)
}
