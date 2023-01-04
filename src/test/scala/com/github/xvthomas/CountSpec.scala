package com.github.xvthomas

import com.github.xvthomas.json.JsonOps
import com.github.xvthomas.json.impl.purescala.{JsonArray, JsonPrinter, PureScalaJsonOpsImpl}
import com.github.xvthomas.model.NamedPeoples
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{OptionValues, TryValues}

// scalastyle:off multiple.string.literals
// scalastyle:off magic.number
// scalastyle:off named.argument
class CountSpec extends AnyFlatSpec with TryValues with OptionValues with should.Matchers {

  val jsonOps: JsonOps = /* new SprayJsonOpsImpl() */ new PureScalaJsonOpsImpl()

  "Counting empty array" should "be not returned" in {
    val root = jsonOps.parse(Fixtures.emptyRoot)
    root.success.value should be(List[NamedPeoples]())
    jsonOps.prettyPrint(ProcessOps.count(root.get)) should be("")
  }

  "Counting root with no animals" should "produce names with count equals to 0" in {
    val root = jsonOps.parse(Fixtures.noAnimals)
    root should be a Symbol("success")
    val res = ProcessOps.count(root.get)
    res.head.name.value should be("Uzuzozne [0]")
    res.head.peoples.value.head.name.value should be("Lillie Abbott [0]")
    res(1).name.value should be("Satanwi [0]")
    res(1).peoples.value.head.name.value should be("Anthony Bruno [0]")
    res(2).name.value should be("Dillauti [0]")
    res(2).peoples.value.head.name.value should be("Winifred Graham [0]")
    res(2).peoples.value(1).name.value should be("Blanche Viciani [0]")
  }

  "Counting spec example root" should "produce names with expected count" in {
    val root = jsonOps.parse(Fixtures.specExample)
    root should be a Symbol("success")
    val res = ProcessOps.count(root.get)
    res.head.name.value should be("Uzuzozne [1]")
    res.head.peoples.value.head.name.value should be("Lillie Abbott [1]")
    res(1).name.value should be("Satanwi [1]")
    res(1).peoples.value.head.name.value should be("Anthony Bruno [1]")
    res(2).name.value should be("Dillauti [14]")
    res(2).peoples.value.head.name.value should be("Winifred Graham [6]")
    res(2).peoples.value(1).name.value should be("Blanche Viciani [8]")
  }
}
