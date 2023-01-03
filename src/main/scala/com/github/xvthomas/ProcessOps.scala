package com.github.xvthomas

import com.github.xvthomas.model._

object ProcessOps {

  /**
   *  filter the Animals by name containing a pattern
   *  - The parents level of the element will be displayed to keep the data structure
   *  - The order should be kept intact
   *  @param pattern The searched string
   *  @param root A list of NamedPeoples
   *  @return A list of NamedPeoples
   */
  def filter(pattern: String, root: List[NamedPeoples]): List[NamedPeoples] = {

    // check if animal name matches with pattern
    def filterAnimal(animal: Animal): Boolean = animal.name.fold(ifEmpty = false)(_.contains(pattern))

    // Keep animal elements which match pattern
    def filterAnimals(animals: List[Animal]): List[Animal] = animals.filter(animal => filterAnimal(animal))

    // keep people and only matched animals
    def filterPeople(people: People): Option[People] = people.animals match {
      case Some(animals) =>
        val filteredAnimals = filterAnimals(animals)
        if (filteredAnimals.isEmpty) None else Some(people.copy(animals = Some(filteredAnimals)))
      case None => None
    }

    // keep named people which contains people containing animals which match pattern
    def filterNamedPeoples(namedPeoples: NamedPeoples): Option[NamedPeoples] = namedPeoples.peoples match {
      case Some(peoples) =>
        val filteredPeoples = peoples.flatMap(filterPeople)
        if (filteredPeoples.isEmpty) None else Some(namedPeoples.copy(peoples = Some(filteredPeoples)))
      case None => None
    }

    root.flatMap(filterNamedPeoples)
  }

  /**
   *  Count the number of children at each level (People, NamedPeoples) of the arborescence and appending it in the name, eg. Satanwi [2]
   *  @param root A list of NamedPeoples
   *  @return A list of NamedPeoples with count added to People.name and to NamedPeople.name attributes found
   */
  def count(root: List[NamedPeoples]): List[NamedPeoples] = {

    // append count to people and namedPeoples according to pattern "name [count]" id name is not empty, otherwise to pattern "[count]"
    def appendCountToName(name: Option[String], count: Int) = s"${name.fold("")(name => s"$name")} [$count]"

    def countPeople(people: People): (People, Int) = {
      val countAnimals: Int = people.animals.fold(ifEmpty = 0)(_.length)
      (people.copy(name = Some(appendCountToName(people.name, countAnimals))), countAnimals)
    }

    def countNamedPeoples(namedPeoples: NamedPeoples): NamedPeoples =
      namedPeoples.peoples match {
        case Some(peoples) =>
          val countPeoples: List[(People, Int)] = peoples.map(countPeople)
          NamedPeoples(
            name = Some(appendCountToName(namedPeoples.name, countPeoples.map(_._2).sum)),
            peoples = Some(countPeoples.map(_._1))
          )
        case None =>
          namedPeoples.copy(name = Some(appendCountToName(namedPeoples.name, count = 0)))
      }

    root.map(countNamedPeoples)
  }
}
