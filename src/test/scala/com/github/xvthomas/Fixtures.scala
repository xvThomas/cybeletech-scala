package com.github.xvthomas

object Fixtures {

  // for sake of clarity, attribute "people" is renamed to "peoples"

  val SpecExample: String = """[
                              |  {
                              |    "name": "Uzuzozne",
                              |    "peoples": [
                              |      {
                              |        "name": "Lillie Abbott",
                              |        "animals": [
                              |          {
                              |            "name": "John Dory"
                              |          }
                              |        ]
                              |      }
                              |    ]
                              |  },
                              |  {
                              |    "name": "Satanwi",
                              |    "peoples": [
                              |      {
                              |        "name": "Anthony Bruno",
                              |        "animals": [
                              |          {
                              |            "name": "Oryx"
                              |          }
                              |        ]
                              |      }
                              |    ]
                              |  },
                              |  {
                              |    "name": "Dillauti",
                              |    "peoples": [
                              |      {
                              |        "name": "Winifred Graham",
                              |        "animals": [
                              |          {
                              |            "name": "Anoa"
                              |          },
                              |          {
                              |            "name": "Duck"
                              |          },
                              |          {
                              |            "name": "Narwhal"
                              |          },
                              |          {
                              |            "name": "Badger"
                              |          },
                              |          {
                              |            "name": "Cobra"
                              |          },
                              |          {
                              |            "name": "Crow"
                              |          }
                              |        ]
                              |      },
                              |      {
                              |        "name": "Blanche Viciani",
                              |        "animals": [
                              |          {
                              |            "name": "Barbet"
                              |          },
                              |          {
                              |            "name": "Rhea"
                              |          },
                              |          {
                              |            "name": "Snakes"
                              |          },
                              |          {
                              |            "name": "Antelope"
                              |          },
                              |          {
                              |            "name": "Echidna"
                              |          },
                              |          {
                              |            "name": "Crow"
                              |          },
                              |          {
                              |            "name": "Guinea Fowl"
                              |          },
                              |          {
                              |            "name": "Deer Mouse"
                              |          }
                              |        ]
                              |      }
                              |    ]
                              |  }
                              |]""".stripMargin.filter(_ >= ' ')

  val NoAnimals: String = """[
                              |  {
                              |    "name": "Uzuzozne",
                              |    "peoples": [
                              |      {
                              |        "name": "Lillie Abbott",
                              |        "animals": [
                             |        ]
                              |      }
                              |    ]
                              |  },
                              |  {
                              |    "name": "Satanwi",
                              |    "peoples": [
                              |      {
                              |        "name": "Anthony Bruno",
                              |        "animals": [
                             |        ]
                              |      }
                              |    ]
                              |  },
                              |  {
                              |    "name": "Dillauti",
                              |    "peoples": [
                              |      {
                              |        "name": "Winifred Graham",
                              |        "animals": [
                              |        ]
                              |      },
                              |      {
                              |        "name": "Blanche Viciani",
                              |        "animals": [
                             |        ]
                              |      }
                              |    ]
                              |  }
                              |]""".stripMargin.filter(_ >= ' ')

  val EmptyRoot: String = """[
                            |]""".stripMargin.filter(_ >= ' ')
}
