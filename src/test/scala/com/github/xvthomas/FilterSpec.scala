package com.github.xvthomas

import com.github.xvthomas.json.JsonOps
import com.github.xvthomas.json.impl.spray.SprayJsonOpsImpl
import com.github.xvthomas.model._
import org.scalatest.TryValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

// scalastyle:off multiple.string.literals
// scalastyle:off magic.number
// scalastyle:off named.argument
class FilterSpec extends AnyFlatSpec with TryValues with should.Matchers {

  val jsonOps: JsonOps = new SprayJsonOpsImpl()

  "The spec test with pattern (ry)" should "succeed with results" in {
    val pattern = "ry"
    val input   = jsonOps.parse(Fixtures.specExample)
    input should be a Symbol("success")

    ProcessOps.filter(pattern, input.get) should be(
      List(
        NamedPeoples(Some("Uzuzozne"), Some(List(People(Some("Lillie Abbott"), Some(List(Animal(Some("John Dory")))))))),
        NamedPeoples(Some("Satanwi"), Some(List(People(Some("Anthony Bruno"), Some(List(Animal(Some("Oryx"))))))))
      )
    )
  }

  "Filtering with pattern (Dory)" should "succeed with results" in {
    val pattern = "Dory"
    val input   = jsonOps.parse(Fixtures.specExample)
    input should be a Symbol("success")
    ProcessOps.filter(pattern, input.get) should be(
      List(NamedPeoples(Some("Uzuzozne"), Some(List(People(Some("Lillie Abbott"), Some(List(Animal(Some("John Dory")))))))))
    )
  }

  "Filtering with pattern (Dory)" should "produce an empty list" in {
    val pattern = "Doe"
    val root    = jsonOps.parse(Fixtures.specExample)
    ProcessOps.filter(pattern, root.get) should be(List())
  }

  "Filtering with pattern (row)" should "succeed with results" in {
    val pattern = "row"
    val root    = jsonOps.parse(Fixtures.specExample)
    ProcessOps.filter(pattern, root.get) should be(List(NamedPeoples(
      Some("Dillauti"),
      Some(List(
        People(Some("Winifred Graham"), Some(List(Animal(Some("Crow"))))),
        People(Some("Blanche Viciani"), Some(List(Animal(Some("Crow")))))
      ))
    )))
  }

  "Empty filtered array" should "be not returned" in {
    val pattern = "Doe"
    val root    = jsonOps.parse(Fixtures.specExample)
    root should be a Symbol("success")
    jsonOps.prettyPrint(ProcessOps.filter(pattern, root.get)) should be("")
  }
}
