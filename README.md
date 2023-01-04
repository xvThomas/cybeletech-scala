![Warning](https://img.shields.io/badge/%20-Uncompleted!-orange)
![Scala](https://img.shields.io/badge/%20-scala-red)
![SBT](https://img.shields.io/badge/%20-sbt-lightgray)

# Cybeletech-scala

# 1. Background

This repository has been created following a request from cybeletech. Details are given in the [third chapter](#request).
The project is developped in `scala` and require `sbt` as package manager and build tool.

# 2. Notes on Scala implementation

## 2.1 Code organization summary

- Code fully implemented with functional style in scala (filter and count operations implemented in [ProcessOps.scala](src/main/scala/com/github/xvthomas/ProcessOps.scala))
- As Scala does no longer provide native implementation of json serialization, the spray-json library is used
- However, to fulfill the test requirements an alternate implementation is provided in pure scala. 
- Selection of implementation (native or spray-json based) is made by using IoC pattern
  * `JsonOps` is an interface which declare `parse` and `prettyPrint` functions
  * `SprayJsonOpsImpl` provides implementation of `JsonOps` based on the additional library spray-json
  * `PureScalaJsonOpsImpl`provides implementation of `JsonOps` based on pure scala, without additional library. Notice also that JsonNumber is not implemented because not necessary in this context .
- For sake of clarity, attribute `people` is renamed into `peoples` in the Json example fragments

## 2.2 Run cybeletech-scala

### 2.2.1 Clone repository

```shell script
git clone https://github.com/xvThomas/cybeletech-scala.git
```

### 2.2.2 Install sbt

Depending on your OS, the installation can differs, see [instructions here](https://www.scala-sbt.org/download.html).

### 2.2.3 Compile then run

The following shell commands must be run in the **root project directory**.

```shell script
$ sbt clean compile
```
Run filter: ![TODO](https://img.shields.io/badge/%20-TODO-red)
```shell script
$ sbt run "..."
```
Runt count: ![TODO](https://img.shields.io/badge/%20-TODO-red)
```shell script
$ sbt run "..."
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
[success] Total time: 2 s, completed 3 janv. 2023 à 19:25:51
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
[success] Total time: 7 s, completed 3 janv. 2023 à 19:59:08
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
