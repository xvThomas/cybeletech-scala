val regexp1 = """--filter="(.+)"""".r
val regexp2 = "--count".r
val regexp3 = "(.+)".r

val input = """--filter="cooldoe""""
val input = """--count"""
val input = "a"

input match {
  case regexp1(filter) => filter
  case regexp2()       => "count"
  case regexp3(file)   => file
  case _               => "no match"
}
