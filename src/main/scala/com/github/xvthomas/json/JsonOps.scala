package com.github.xvthomas.json

import com.github.xvthomas.model.NamedPeoples

import scala.util.Try

/**
 *  JsonOps is a trait (interface) and should be jointly used with SprayJsonOps or NativeJsonOps implementation (IoC)
 */
trait JsonOps {

  /**
   *  Parse a json-formatted string as specified in README.md
   *  @param json The json formatted string to be parsed
   *  @return A list of People with underlying Animals
   */
  def parse(json: String): Try[List[NamedPeoples]]

  /**
   *  Print a list of NamedPeoples in json format into a String
   *  If the list is empty, the empty string is returned
   *  @param namedPeoples: the NamedPeople to be printed
   */
  def prettyPrint(namedPeoples: List[NamedPeoples]): String
}
