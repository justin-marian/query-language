import scala.io.Source

object TestTables {
  val table1: Table = new Table(
    List("col1", "col2"), List(
      List("a", "2"),
      List("b", "3"),
      List("c", "4"),
      List("d", "5")
    ))

  // Read Table data from CSV files and remove carriage returns

  val table1String: String = {
    val src = Source.fromFile("tables/table1.csv")
    val str = src.mkString
    src.close()
    str.replace("\r", "")
  }

  val table2: Table = {
    val src = Source.fromFile("tables/table2.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val table3: Table = {
    val src = Source.fromFile("tables/table3.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val table4: Table = {
    val src = Source.fromFile("tables/table4.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val table3_4_merged: Table = {
    val src = Source.fromFile("tables/table3_4_merged.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val test3_newCol_Value: Table = {
    val src = Source.fromFile("tables/test_3_newCol_Value.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val test3_Select_Value: Table = {
    val src = Source.fromFile("tables/test_3_Select_Value.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val test3_Filter_Value: Table = {
    val src = Source.fromFile("tables/test_3_Filter_Value.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val test3_Merge_Value: Table = {
    val src = Source.fromFile("tables/test_3_Merge_Value.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val tableFunctional: Table = {
    val src = Source.fromFile("tables/Functional.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val tableObjectOriented: Table = {
    val src = Source.fromFile("tables/Object-Oriented.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val tableImperative: Table = {
    val src = Source.fromFile("tables/Imperative.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val ref_programmingLanguages1: Table = {
    val src = Source.fromFile("tables/test_3_1.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val ref_programmingLanguages2: Table = {
    val src = Source.fromFile("tables/test_3_2.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  val ref_programmingLanguages3: Table = {
    val src = Source.fromFile("tables/test_3_3.csv")
    val str = src.mkString
    src.close()
    Table(str.replace("\r", ""))
  }

  // Regular empty Table
  val emptyTable: Table = new Table(List.empty, List(List.empty))

  /**
   * FUNC - new column with name "Functional"
   * default value "Yes" using Value() to evaluate the operation and generate a new Table
   * OO, IMP - similar to FUNC but with the column names "Object-Oriented", "Imperative"
   */
  val FUNC: Table = NewCol("Functional", "Yes", Value(tableFunctional)).eval.get
  val OO: Table = NewCol("Object-Oriented", "Yes", Value(tableObjectOriented)).eval.get
  val IMP: Table = NewCol("Imperative", "Yes", Value(tableImperative)).eval.get
  // Merging 3 tables (used twice between OO and IMP, and the merging result with FUNC)
  val programmingLanguages1: Table =
    Merge("Language", Value(FUNC),
    Merge("Language", Value(OO), Value(IMP)))
    .eval.getOrElse(emptyTable) // Default value in case of failure

  // Filter is applied on programmingLanguages1
  val programmingLanguages2: Table =
    Filter(
      line =>
        Some {
          // Takes as input a line in table in case if it doesn't exist, it puts ""
          // Retrieve values for "Original purpose" & "Other paradigms"
          val originalPurpose = line.getOrElse("Original purpose", "")
          val otherParadigms = line.getOrElse("Other paradigms", "")
          // Checks if the values extracted before contains "Applications" & "concurrent"
          originalPurpose.contains("Application") && otherParadigms.contains("concurrent")
        }, Value(programmingLanguages1) // true/false depending on originalPurpose and otherParadigms
    ).eval.getOrElse(emptyTable) // Default value in case of failure

  // Filter is applied on programmingLanguages2
  val programmingLanguages3: Table =
    Select(
      List("Language", "Object-Oriented", "Functional"),
      Filter(
        line =>
          Some {
            // Takes as input a line in table in case if it doesn't exist, it puts ""
            // Retrieve values for "Language", "Object-Oriented", and "Functional"
            val language = line.getOrElse("Language", "")
            val objectOriented = line.getOrElse("Object-Oriented", "")
            val functional = line.getOrElse("Functional", "")
            // It evaluates if there is
            language.nonEmpty || objectOriented.nonEmpty || functional.nonEmpty
        }, Value(programmingLanguages2) // true/false depending on language, objectOriented and functional
      )
    ).eval.getOrElse(emptyTable) // Default value in case of failure
}
