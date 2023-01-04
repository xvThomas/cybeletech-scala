package com.github.xvthomas.json.impl.spray

import com.github.xvthomas.json.JsonOps
import com.github.xvthomas.model._
import spray.json._

import scala.util.Try

/**
 *  Implementation of JsonOps with spray-json
 */
class SprayJsonOpsImpl extends JsonOps with DefaultJsonProtocol {

  // define JsonProtocol for model classes
  implicit val animalFormat: RootJsonFormat[Animal]      = jsonFormat1(Animal)
  implicit val peopleFormat: RootJsonFormat[People]      = jsonFormat2(People)
  implicit val colorFormat: RootJsonFormat[NamedPeoples] = jsonFormat2(NamedPeoples)

  override def parse(json: String): Try[List[NamedPeoples]] = Try(
    json.parseJson.convertTo[List[NamedPeoples]]
  )

  override def prettyPrint(root: List[NamedPeoples]): String =
    if (root.isEmpty) "" else root.toJson.prettyPrint
}
