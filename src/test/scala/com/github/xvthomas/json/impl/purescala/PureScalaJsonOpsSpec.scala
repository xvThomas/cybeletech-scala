package com.github.xvthomas.json.impl.purescala

import com.github.xvthomas.Fixtures
import com.github.xvthomas.model.{Animal, NamedPeoples, People}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{EitherValues, TryValues}

import scala.util.{Failure, Success}

class PureScalaJsonOpsSpec extends AnyFlatSpec with EitherValues with TryValues with should.Matchers {
  val jsonOps: PureScalaJsonOpsImpl = new PureScalaJsonOpsImpl()

  "specExample" should "be correctly converted to List[NamedPeoples]" in {
    jsonOps.parse(Fixtures.specExample).success.value should be(List(
      NamedPeoples(Some("Uzuzozne"), Some(List(People(Some("Lillie Abbott"), Some(List(Animal(Some("John Dory")))))))),
      NamedPeoples(Some("Satanwi"), Some(List(People(Some("Anthony Bruno"), Some(List(Animal(Some("Oryx")))))))),
      NamedPeoples(
        Some("Dillauti"),
        Some(List(
          People(
            Some("Winifred Graham"),
            Some(List(Animal(Some("Anoa")), Animal(Some("Duck")), Animal(Some("Narwhal")), Animal(Some("Badger")), Animal(Some("Cobra")), Animal(Some("Crow"))))
          ),
          People(
            Some("Blanche Viciani"),
            Some(List(
              Animal(Some("Barbet")),
              Animal(Some("Rhea")),
              Animal(Some("Snakes")),
              Animal(Some("Antelope")),
              Animal(Some("Echidna")),
              Animal(Some("Crow")),
              Animal(Some("Guinea Fowl")),
              Animal(Some("Deer Mouse"))
            ))
          )
        ))
      )
    ))
  }

  "animal json object" should "be correctly converted to Animal" in {
    val json = """{ "name": "Oryx" }"""
    (JsonParser.parse(json).value match {
      case jsonObject: JsonObject => jsonOps.convertToAnimal(jsonObject)
      case _                      => fail()
    }) should be(Right(Animal(Some("Oryx"))))
  }

  "animal json object with null value" should "be correctly converted to Animal" in {
    val json = """{ "name": "null" }"""
    (JsonParser.parse(json).value match {
      case jsonObject: JsonObject => jsonOps.convertToAnimal(jsonObject)
      case _                      => fail()
    }) should be(Right(Animal(None)))
  }

  "animal json empty object" should "not be converted to Animal" in {
    val json = """{}"""
    (JsonParser.parse(json).value match {
      case jsonObject: JsonObject => jsonOps.convertToAnimal(jsonObject)
      case _                      => fail()
    }) should be(Right(Animal(None)))
  }

  "several animals json array" should "correctly parsed to list of Animal" in {
    val json = """[ { "name": "Oryx" }, { "name": "Narwhal" }, { "name": "Cobra" } ]"""
    (JsonParser.parse(json).value match {
      case jsonArray: JsonArray => jsonOps.convertToAnimals(jsonArray)
      case _                    => fail()
    }) should be(Right(List(Animal(Some("Oryx")), Animal(Some("Narwhal")), Animal(Some("Cobra")))))
  }

  "parsed and printed specExample" should "have the same internal representation than specExample" in {
    jsonOps.parse(Fixtures.specExample) match {
      case Failure(_) => fail()
      case Success(value) =>
        println(jsonOps.prettyPrint(value))
        jsonOps.parse(jsonOps.prettyPrint(value)) should be(jsonOps.parse(Fixtures.specExample))
    }
  }
}
