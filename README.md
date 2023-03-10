[![Scala CI](https://github.com/xvThomas/cybeletech-scala/actions/workflows/scala.yml/badge.svg)](https://github.com/xvThomas/cybeletech-scala/actions/workflows/scala.yml)
[![Coverage Status](https://coveralls.io/repos/github/xvThomas/cybeletech-scala/badge.svg?branch=master)](https://coveralls.io/github/xvThomas/cybeletech-scala?branch=master)
![Scala](https://img.shields.io/badge/scala-2.13.8-red)
![SBT](https://img.shields.io/badge/sbt-1.8.0-lightgray)

# Cybeletech-scala

# 1. Background

This repository has been created following a request from Cybeletech. Details are given in the [third chapter](#request).
The project is developed in [scala](https://www.scala-lang.org/) and requires [sbt](https://www.scala-sbt.org/) as package manager and for build and run.
So far no binary package is provided (fat jar, docker image, etc.)

# 2. Notes on Scala implementation

## 2.1 Code organization summary

- Code fully implemented with functional style in scala. **Filter** and **Count** operations are implemented in [ProcessOps.scala](src/main/scala/com/github/xvthomas/ProcessOps.scala).
- As Scala does no longer provide native implementation of json serialization, the spray-json library is used.
- **However**, to fulfill the test requirements an **alternate** implementation is provided in **pure scala**. 
- Selection of implementation (pure or spray-json based) is made by using IoC pattern:
  * [JsonOps](src/main/scala/com/github/xvthomas/json/JsonOps.scala) is an interface which declare `parse` and `prettyPrint` functions.
  * [SprayJsonOpsImpl](src/main/scala/com/github/xvthomas/json/impl/spray/SprayJsonOpsImpl.scala) provides implementation of `JsonOps` based on the additional [spray-json](https://github.com/spray/spray-json) library.
  * [PureScalaJsonOpsImpl](src/main/scala/com/github/xvthomas/json/impl/purescala/PureScalaJsonOpsImpl.scala) provides implementation of `JsonOps` based on pure scala, without additional library.


### 2.1.2 Json schema

Starting from the given json fragments, the following json schema is considered:
(For sake of clarity, **attribute `people` is renamed into `peoples`**).
```json
{
  "$name" : { "type": "string"},
          
  "$namedPeoples": {
    "type": "object",
    "properties": {
      "name": {
        "$ref": "#/$name"
      },
      "peoples": {
        "type": "array",
        "items": {
          "$ref": "#/$people"
        }
      }
    }
  },
  
  "$people": {
    "type": "object",
    "properties": {
      "name": {
        "$ref": "#/$name"
      },
      "animals": {
        "type": "array",
        "items": {
          "$ref": "#/$animals"
        }
      }
    }
  },
  
  "$animals": {
    "type": "object",
    "properties": {
      "name": {
        "$ref": "#/$name"
      }
    }
  },
  
  "type": "array",
  "items": { "$ref":  "#/$namedPeoples" }
}
```

The scala model classes [Animal](src/main/scala/com/github/xvthomas/model/Animal.scala), [People](src/main/scala/com/github/xvthomas/model/People.scala) and [NamedPeoples](src/main/scala/com/github/xvthomas/model/NamedPeoples.scala) fit with the json schema.

### 2.1.3 Implementation of json Parser (PureScalaJsonOpsImpl)

As the McKeeman Form of the official [Json grammar](https://www.json.org/json-en.html) is not LL(1), the following Backus-Naur-Form is used in implementation:

- Lexical analysis
```
boolean ::= "true" | "false"
null    ::= "null"
string  ::= regexp("\"((?:[^\"\\\\/\b\f\n\r\t]|\\\\u\\d{4})*)\"")
```
- Syntactic analysis
```
key      ::= string
value    ::= string | array | object | boolean | null
member   ::= key ":" value
members  ::= member | member "," members
object   ::= "{" [members] "}"
elements ::= value | value "," elements
array    ::= "[" [elements] "]"
```
- Notice that `Number` is not implemented since not required in this context.
- During parsing, the json string is interpreted as Json Abstract Syntax Tree, then converted into the model scala classes (straightforward with spray-json).
- Filter and Count Operations use the model scala class tree as input.
- Operations output is represented using the model scala class tree, then converted into json string.

## 2.2 Run cybeletech-scala

### 2.2.1 Getting source code

```shell script
git clone https://github.com/xvThomas/cybeletech-scala.git
```

### 2.2.2 Install sbt

Depending on your OS, the installation can differs, see [instructions here](https://www.scala-sbt.org/download.html).

### 2.2.3 Compile then run

The following shell commands must be executed in the **root project directory**.
Java SDK or JDK must be available on the running host (tested with java version 11.0.9.1)

```shell script
$ sbt clean compile
```
- Usage (type the following command):
```shell script
$ sbt run
...
Usage: run [filter | count] file

Filter command:
--filter="<pattern>"   filter json file using a pattern
count command:
--count                count animals

Examples:
$ sbt "run --filter=\"ry\" ./doc/data.json"
$ sbt "run --count ./doc/data.json"
```

Run filter:
```shell script
$ sbt "run --filter=\"<pattern>\" <file>
```
Run count:
```shell script
$ sbt "run --count <file>
```

## 2.3 Tests

### 2.3.1 Simple tests
To start unit tests, run:
```shell script
$ sbt clean test
```

### 2.3.2 Tests with coverage
- Run tests:
```shell script
$ sbt clean coverage test
```
- Print report:
```shell script
$ sbt coverageReport
...
[info] Statement coverage.: 83.41%
[info] Branch coverage....: 79.17%
[info] Coverage reports completed
[info] All done. Coverage was stmt=[83.41%] branch=[79.17%]
[success] Total time: 2 s, completed 3 janv. 2023 ?? 19:25:51
```

## 2.4 Code quality

### 2.4.1 Code style
This project use [scalastyle](http://www.scalastyle.org) as scala code syntax checker:
```shell script
$ sbt scalastyle
```

### 2.4.2 Code analyzer
The project code leverages [scapegoat](https://github.com/scapegoat-scala/scapegoat) scala static code analyzer:
```shell script
$ sbt scapegoat
...
[info] [info] [scapegoat] 118 activated inspections
[info] [info] [scapegoat] Analysis complete: 13 files - 0 errors 0 warns 0 infos
[info] [info] [scapegoat] Written HTML report [/Users/xavierthomas/Documents/dev/com.github.xvthomas/cybeletech-scala/target/scala-2.13/scapegoat-report/scapegoat.html]
[info] [info] [scapegoat] Written XML report [/Users/xavierthomas/Documents/dev/com.github.xvthomas/cybeletech-scala/target/scala-2.13/scapegoat-report/scapegoat.xml]
[info] [info] [scapegoat] Written Scalastyle XML report [/Users/xavierthomas/Documents/dev/com.github.xvthomas/cybeletech-scala/target/scala-2.13/scapegoat-report/scapegoat-scalastyle.xml]
[info] [info] [scapegoat] Written Markdown report [/Users/xavierthomas/Documents/dev/com.github.xvthomas/cybeletech-scala/target/scala-2.13/scapegoat-report/scapegoat.md]
[success] Total time: 7 s, completed 3 janv. 2023 ?? 19:59:08
```

# <a name="request"></a> 3. Javascript backend developer test

Your job is to write a command-line interface in your choice langage.

## Filter
In the attached file `data.js`, there are `Peoples` containing `Animals`.

This option has to filter the Animals by name containing a pattern. The parents level of the element will be displayed to keep the data structure. The order should be kept intact.
Empty array after filtering are NOT returned.

Sample of running the command, and its output:
Here, only animals containing `ry` are displayed.

```shell script
$ node app.js --filter=ry
[
  {
    name: 'Uzuzozne',
    people: [
      {
        name: 'Lillie Abbott',
        animals: [
          {
            name: 'John Dory'
          }
        ]
      }
    ]
  },
  {
    name: 'Satanwi',
    people: [
      {
        name: 'Anthony Bruno',
        animals: [
          {
            name: 'Oryx'
          }
        ]
      }
    ]
  }
]
```

## Count

The next goal is to print the counts of People and Animals by counting the number of children at each level of the arborescence and appending it in the name, eg. `Satanwi [2]`.

Sample of running the command, and its output:

```shell script
node app.js --count
[ { name: 'Dillauti [16]',
    people:
     [ { name: 'Winifred Graham [6]',
         animals:
          [ { name: 'Anoa' },
            { name: 'Duck' },
            { name: 'Narwhal' },
            { name: 'Badger' },
            { name: 'Cobra' },
            { name: 'Crow' } ] },
       { name: 'Blanche Viciani [8]',
         animals:
          [ { name: 'Barbet' },
            { name: 'Rhea' },
            { name: 'Snakes' },
            { name: 'Antelope' },
            { name: 'Echidna' },
            { name: 'Crow' },
            { name: 'Guinea Fowl' },
            { name: 'Deer Mouse' } ] },
      ...
...
]
```

## Requirements

- The code must be available in a GIT repository
- No library/modules should be used, except for the testing library

## Appreciation

We will be really attentive to:

- Code readability, structure and consistency
- Tests, and how they are written
- Overall App structure for production usage
