![Scala](https://img.shields.io/badge/scala-%23DC322F.svg?style=for-the-badge&logo=scala&logoColor=white)

# 1. Background

This repository has been created following a request from cybeletech. Details are given in the [last chapter](#request).
The project is developped in `scala` and require `sbt` as package manager and build tool.

# 2. Notes on Scala implementation

## Code organization summary

- Code fully implemented with functional style in scala (filter and count operations)
- As Scala does no longer provide native implementation of json serialization, the spray-json library is used
- However, to fulfill the test requirements an alternate implementation is provided in pure scala. 
- Selection of implementation (native or spray-json based) is made by using IoC pattern
  * `JsonOps` is an interface which declare `parse` and `prettyPrint` functions
  * `SprayJsonOpsImpl` provides implementation of `JsonOps` based on the additional library spray-json
  * `PureScalaJsonOpsImpl`provides implementation of `JsonOps` based on pure scala, without additional library
- For sake of clarity, attribute `people` is renamed into `peoples` in the Json example fragments

## Run cybeletech-scala

### Clone repository

### Install sbt

### Compile then run
```shell script
$ sbt clean compile
```
Run filter
```shell script
$ sbt run todo
```
Runt count:
```shell script
$ sbt clean compile
```

## Tests

### Simple tests
To start unit tests, run:
```shell script
$ sbt clean test
```

### Tests with coverage
- Run tests:
```shell script
$ sbt clean coverage test
```
- Print report
```shell script
$ sbt coverageReport
...
[info] Statement coverage.: 83.41%
[info] Branch coverage....: 79.17%
[info] Coverage reports completed
[info] All done. Coverage was stmt=[83.41%] branch=[79.17%]
[success] Total time: 2 s, completed 3 janv. 2023 à 19:25:51
```

## Linters

### Code style
This project use [scalastyle](http://www.scalastyle.org) as scala code syntax checker:
```shell script
$ sbt scalastyle
```

### Code analyzer
The project code leverages [scapegoat](https://github.com/scapegoat-scala/scapegoat) scala static code analyzer:
```shell script
$ sbt scapegoat
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
