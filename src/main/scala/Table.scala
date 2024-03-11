import util.Util.{Line, Row}
import scala.annotation.tailrec

trait FilterCond {
  def && (other: FilterCond): FilterCond = And(this, other)
  def || (other: FilterCond): FilterCond = Or(this, other)
  def eval(r: Row): Option[Boolean] // Fails if the column name is not present in the row
}

case class Field(colName: String, predicate: String => Boolean) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    // Verify if the Row exists then we apply the predicate
    r.get(colName) match {
      case Some(value) => Some(predicate(value))
      case _ => None
    }
  }
}

case class And(f1: FilterCond, f2: FilterCond) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    // Evaluates filter conditions f1 and f2 by a specified Row
    val res1 = f1.eval(r)
    val res2 = f2.eval(r)
    // Applies logical AND between f1 and f2
    (res1, res2) match {
      case (Some(bool1), Some(bool2)) => Some(bool1 && bool2)
      case _ => None
    }
  }
}

case class Or(f1: FilterCond, f2: FilterCond) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    // Evaluates filter conditions f1 and f2 by a specified Row
    val res1 = f1.eval(r)
    val res2 = f2.eval(r)
    // Applies logical OR between f1 and f2
    (res1, res2) match {
      case (Some(bool1), Some(bool2)) => Some(bool1 || bool2)
      case _ => None
    }
  }
}

trait Query {
  def eval: Option[Table]
}

/*
  Atom query which evaluates to the input table - Always succeeds
 */
case class Value(t: Table) extends Query {
  // Value query which evaluates an input Table at Option[Table]
  override def eval: Option[Table] = Some(t)
}

/*
  Selects certain columns from the result of a target query
  Fails with None if some rows are not present in the resulting table
 */
case class Select(columns: Line, target: Query) extends Query {
  override def eval: Option[Table] = {
    // Query evaluates the target first
    val targetResult = target.eval
    // Applies on a specified column which results in a new table
    targetResult.flatMap {
      case table : Table => table.select(columns)
      case _ => None
    }
  }
}

/*
  Filters rows from the result of the target query
  Success depends only on the success of the target
 */
case class Filter(condition: FilterCond, target: Query) extends Query {
  override def eval: Option[Table] = {
    // Query evaluates the target first
    val targetResult = target.eval
    // Applies filter on a specified column which results in a new table
    targetResult.flatMap {
      // Specified condition which results in a new table
      case table: Table => table.filter(condition)
      case _ => None
    }
  }
}

/*
  Creates a new column with default values
  Success depends only on the success of the target
 */
case class NewCol(name: String, defaultVal: String, target: Query) extends Query {
  override def eval: Option[Table] = {
    // Query evaluates the target first
    val targetResult = target.eval
    // Applies newCol with a specified column name which results in a new table
    targetResult.map {
      table : Table => table.newCol(name, defaultVal)
    }
  }
}

/*
  Combines two tables based on a common key
  Success depends on whether the key exists in both tables or not AND on the success of the target
 */
case class Merge(key: String, t1: Query, t2: Query) extends Query {
  override def eval: Option[Table] = {
    // Evaluates filter conditions f1 and f2 by a specified Row
    val t1Res = t1.eval
    val t2Res = t2.eval
    // Merge query evaluates the targets first t1 & t2
    // Applies merge on a specified column name in the result will have a new table
    (t1Res, t2Res) match {
      case (Some(t_1), Some(t_2)) =>
        val mergedTable = t_1.merge(key, t_2)
        mergedTable
      case _ =>
        None
    }
  }
}

class Table (columnNames: Line, tabular: List[List[String]]) {
  def getColumnNames: Line = columnNames
  def getTabular: List[List[String]] = tabular

  override def toString: String = {
    // Separate lines by "\n" and add to each element of the table "," -> CSV format
    val lines = this.getTabular.map(_.mkString(",")).mkString("\n")
    // Foreach colum add with "," to separate each row by comma
    val csv_table = this.getColumnNames.mkString(",") + "\n" + lines
    // Return CSV table as a string format
    csv_table
  }

  def select(columns: Line): Option[Table] = {
    // Check all columns if they exist in the csv column names table
    val errColumns = columns.filter(column => !this.getColumnNames.contains(column))
    // In case if there are no column names
    if (errColumns.nonEmpty)
      None
    else {
      // Select only the lines from each column from the csv data lines
      val dataLines = this.getTabular.map(line => line.zip(this.getColumnNames).filter {
        case (_, columnName) => columns.contains(columnName)
      }.map(_._1)) // map the corresponding value
      // Return CSV Table with the selected columns and column names
      Some(new Table(columns, dataLines))
    }
  }

  /* Conversion from Line to Row */
  def rowToRowMap(line: Line): Row = {
    this.getColumnNames.zip(line).toMap
  }

  def filter(cond: FilterCond): Option[Table] = {
    /* Condition that checks if there are lines
       that respects the condition, return a new table
       which contains those lines created by rowToRowMap */
    val filteredTabular = tabular.filter {
      line => cond.eval(rowToRowMap(line)).getOrElse(false)
    }
    if (filteredTabular.isEmpty)
      None
    else
      Some(new Table(this.getColumnNames, filteredTabular))
  }

  def newCol(name: String, defaultVal: String): Table = {
    // If column already in the table
    if (this.getColumnNames.contains(name))
      this
    else {
      // Add a column name at the end
      val newColumnNames = this.getColumnNames ++ List(name)
      // Add at each line of the table the default value
      val newTable = this.getTabular.map(line => line ++ List(defaultVal))
      new Table(newColumnNames, newTable)
    }
  }

  // Helper method: iterates through the rows in the tabular
  private def createKeyMap(table: Table, key: String): Map[String, Line] = {
    @tailrec
    // accumulates a Map where the keys are the values in the column
    // specified by "key" and the values corresponding to lines in the tabular
    def accCreateKeyMap(lines: List[Line], acc: Map[String, Line]): Map[String, Line] = {
      lines match {
        case Nil => acc // Base Case : no more column names to check after the key, final result
        case colNames :: tabular => // General Case : extracts the column names of the current line
          val keyLine = colNames(table.getColumnNames.indexOf(key))
          val newLine = keyLine -> colNames // K-V is added to the existing lines
          accCreateKeyMap(tabular, acc + newLine)
      }
    }
    accCreateKeyMap(table.getTabular, Map.empty)
  }

  /*
     Merge 2 tables based on there column names and a specific key
   */
  def merge(key: String, other: Table): Option[Table] = {
    // Verify if the key exists in one of tables
    if (!this.columnNames.contains(key) || !other.getColumnNames.contains(key))
      return None

    // Mapping the values with there corresponding key.
    // In case if it doesn't exist is added to line column names
    val currMapVals = createKeyMap(this, key)
    val otherMapVals = createKeyMap(other, key)

    // Column names are associated with the key value from each element of the line
    val mergedColumnNames = currMapVals.keys ++ otherMapVals.keys
    // Excluding the column names that are in both tables, make sure that there are no duplicates
    val newColumnNames = other.getColumnNames.filterNot(this.getColumnNames.contains)

    /* Helper method that takes a key k and returns a merged line for the given key k */
    def mergedLine(k: String) : List[String]  = {
      /* Returns a line with the values inside of it in case if the key k
         is associate with the values return the line itself in case it is not in we add "" */
      val currLine = currMapVals.getOrElse(k, List.fill(this.getColumnNames.size)(""))
      val otherLine = otherMapVals.getOrElse(k, List.fill(other.getColumnNames.size)(""))

      // Merging the column names for both tables
      val mergedColumns = this.getColumnNames ++ newColumnNames

      /** Create a new list of merged values for each column
       * retrieve the value(s) corresponding to the given column from the current line
       * by combining the values of the line with column names
       * to create a new pair of values corresponding to the column names
       ----------------------------------------------------------------------------------
       *  Find the pair where the column name is equal to the given column
       * for both values from the lines tables at the end, extract the value,
       * and if it's not found, return an empty string ""
       */
      mergedColumns.map {
        // Take each column from mergedColumns
        column =>
          // Current value(s) for the current line and for the other line from this and other table
          val currValName = currLine.zip(this.getColumnNames).find(_._2 == column).map(_._1).getOrElse("")
          val otherValName  = otherLine.zip(other.getColumnNames).find(_._2 == column).map(_._1).getOrElse("")
          // Determine merged value
          if (currValName == otherValName) currValName // Both have the same value => no duplicates
          else if (otherValName.nonEmpty && currValName.nonEmpty) s"$currValName;$otherValName" // Different values
          else if (otherValName.isEmpty && currValName.nonEmpty) currValName
          else if (currValName.isEmpty && otherValName.nonEmpty) otherValName
          else  "" // Default case if both of them have value "" associated with column name
      }
    }
    Some(new Table(this.getColumnNames ++ newColumnNames, mergedColumnNames.toList.map(mergedLine)))
  }
}

object Table {
  // Splits the string into a list of strings separated by commas
  def splitComma(s: String): List[String] = {
    s.split(",", -1).toList
  }

  def apply(s: String): Table = {
    // Split the input string by newline to get lines
    val lines = s.split("\n").toList
    // Split the first line by comma to get column names
    val nameColumns = splitComma(lines.head)
    // Split the rest of the lines by comma to get data lines
    val dataLines = lines.tail.map(splitComma)
    // Create the Table object with the column names and data lines
    new Table(nameColumns, dataLines)
  }
}