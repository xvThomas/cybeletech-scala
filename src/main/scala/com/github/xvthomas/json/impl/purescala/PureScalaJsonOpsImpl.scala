package com.github.xvthomas.json.impl.purescala

import com.github.xvthomas.json.JsonOps
import com.github.xvthomas.model.{Animal, NamedPeoples, People}

import scala.util.{Failure, Success, Try}

/**
 *  Pure scala Implementation of JsonOps
 */
class PureScalaJsonOpsImpl extends JsonOps {

  private val nameKey    = "name"
  private val peoplesKey = "peoples"
  private val animalsKey = "animals"

  override def parse(json: String): Try[List[NamedPeoples]] = JsonParser.parse(json) match {
    case Left(error) => Failure(new java.lang.RuntimeException(error.toString))
    case Right(jsonArray) => jsonArray match {
        case jsonArray: JsonArray =>
          convertToRoot(jsonArray) match {
            case Left(errors) => Failure(new java.lang.RuntimeException(errors.mkString(",")))
            case Right(value) => Success(value)
          }
        case _ => Failure(new java.lang.RuntimeException("JsonArray of namedPeople expected"))
      }
  }

  private def eitherT[A, B](list: List[Either[A, B]]): Either[List[A], List[B]] =
    list.collect { case Left(a) => a } match {
      case ::(head, next) => Left(head +: next)
      case Nil            => Right(list.collect { case Right(b) => b })
    }

  private def eitherLT[A, B](list: List[Either[List[A], B]]): Either[List[A], List[B]] =
    list.collect { case Left(a) => a } match {
      case ::(head, next) => Left((head +: next).flatten)
      case Nil            => Right(list.collect { case Right(b) => b })
    }

  private[purescala] def convertToRoot(jsonArray: JsonArray): Either[List[String], List[NamedPeoples]] =
    eitherLT(jsonArray.value.map {
      case jsonObject: JsonObject => convertToNamedPeoples(jsonObject)
      case jsonValue              => Left(List(s"NamedPeople JsonObject expected instead of $jsonValue"))
    })

  private[purescala] def convertToNamedPeoples(jsonObject: JsonObject): Either[List[String], NamedPeoples] = {
    for {
      nameMember <- jsonObject.value.get(nameKey) match {
        case None => Right(None)
        case Some(value) => value match {
            case JsonString(value) => Right(Some(value))
            case JsonNull          => Right(None)
            case _                 => Left(List("JsonString or JsonNull expected in namedPeoples name"))
          }
      }
      peoplesMember <- jsonObject.value.get(peoplesKey) match {
        case None => Right(None)
        case Some(value) => value match {
            case JsonNull             => Right(None)
            case jsonArray: JsonArray => Right(Some(jsonArray))
            case _                    => Left(List("JsonArray or JsonNull expected in namedPeoples peoples"))
          }
      }
      aa <- peoplesMember match {
        case Some(value) => convertToPeoples(value).map(Option(_))
        case None        => Right(None)
      }
    } yield NamedPeoples(nameMember, aa)
  }

  private[purescala] def convertToPeoples(jsonArray: JsonArray): Either[List[String], List[People]] = {
    eitherLT(jsonArray.value.map {
      case jsonObject: JsonObject => convertToPeople(jsonObject)
      case jsonValue              => Left(List(s"People JsonObject expected instead of $jsonValue"))
    })
  }

  private[purescala] def convertToPeople(jsonObject: JsonObject): Either[List[String], People] = {
    for {
      nameMember <- jsonObject.value.get(nameKey) match {
        case None => Right(None)
        case Some(value) => value match {
            case JsonString(value) => Right(Some(value))
            case JsonNull          => Right(None)
            case _                 => Left(List("JsonString or JsonNull expected in people name"))
          }
      }
      animalsMember <- jsonObject.value.get(animalsKey) match {
        case None => Right(None)
        case Some(value) => value match {
            case JsonNull             => Right(None)
            case jsonArray: JsonArray => Right(Some(jsonArray))
            case _                    => Left(List("JsonArray or JsonNull expected in people animal"))
          }
      }
      animals <- animalsMember match {
        case Some(value) => convertToAnimals(value).map(Option(_))
        case None        => Right(None)
      }
    } yield People(nameMember, animals)
  }

  private[purescala] def convertToAnimals(jsonArray: JsonArray): Either[List[String], List[Animal]] = {
    eitherT(jsonArray.value.map {
      case jsonObject: JsonObject => convertToAnimal(jsonObject)
      case jsonValue              => Left(s"Animal JsonObject expected instead of $jsonValue")
    })
  }

  private[purescala] def convertToAnimal(jsonObject: JsonObject): Either[String, Animal] =
    jsonObject.value.get(nameKey) match {
      case None => Right(Animal(None))
      case Some(value) => value match {
          case JsonString(value) => Right(Animal(Some(value)))
          case JsonNull          => Right(Animal(None))
          case _                 => Left("JsonString or JsonNull expected in animal name")
        }
    }

  override def prettyPrint(root: List[NamedPeoples]): String = {
    if (root.isEmpty) { "" }
    else {
      JsonPrinter.prettyPrint(JsonArray(root.map(namedPeoples =>
        JsonObject(Map(
          nameKey -> namedPeoples.name.fold[JsonValue](JsonNull)(value => JsonString(value)),
          peoplesKey -> namedPeoples.peoples.fold[JsonValue](JsonNull)(peoples =>
            JsonArray(peoples.map(people =>
              JsonObject(Map(
                nameKey -> people.name.fold[JsonValue](JsonNull)(value => JsonString(value)),
                animalsKey -> people.animals.fold[JsonValue](JsonNull)(animals =>
                  JsonArray(animals.map(animal =>
                    JsonObject(Map(
                      nameKey -> animal.name.fold[JsonValue](JsonNull)(value => JsonString(value))
                    ))
                  ))
                )
              ))
            ))
          )
        ))
      )))
    }
  }
}
