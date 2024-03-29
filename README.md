# Query Language

## Description

CSV table operations with custom-defined data types. Objective *is not just to perform operations on tables* but to provide a **foundation for a Query Language** that allows complex data manipulations. Query Language that combines simple operations on tables, thus opening up new ways, enabling customization, optimization, and advanced analysis.

## Table Structure

The `Table` data structure is built to handle tabular data, consisting of `String` cells arranged in rows and columns, the first column names the element types: `Table (columnNames: Line, tabular: List[List[String]])`. Each column in a table has a name to make it easier to work with the data. Main ways to represent a row:

```scala
    type Line = List[String]
    type Row = Map[String, String]
```

- **Line**: Just a list of the values in a row, put in order. It's a simple way to look at row data in case if the user doesn't need to know about the column names.
- **Row**: Map form, where each column name is linked to its value in the row. It's useful for when you need to do operations that depend on knowing which data belongs to which column.

There exists **two** row representations, that give different ways to work with table data, making the `Table` structure flexible for various tasks including sorting, searching, aggregating, and exporting.

- `Line` for straightforward tasks.
- `Row` for detailed work that involves column names.

## Table

Toolkit for simple data table manipulation and CSV file handling:

- **Parsing and Display (`apply`):** Parse input and generate output in CSV format, data exchange and visualization.
- **Column Selection (`select`)**: Extract specific columns from a table based on user input.
- **Column Addition (`newCol`)**: Addition of a new column to the table, populating it with a default value across all rows. This feature is essential for data enrichment and preparing datasets for further analysis.
- **Row Filtering (`filter`)**: Applies user-defined conditions to table rows, retaining only those that meet the criteria. The `filter` function extends to logical operations, supporting `&&` (AND), `||` (OR), and direct column value comparisons, involving cases with non-existent columns.
- **Table Merging (`merge`)**: Merges two tables based on a shared key column. It uniquely handles overlapping values by concatenating them with (`;`), preserving both datasets' integrity, ensuring **no data loss**. For ***unmatched rows***, it fills missing columns with empty strings, ensuring the merged table's completeness. This approach show how to handle data integrity, guaranteeing no loss of valuable information.

## Query Structure

The grammar defines a query language for operations on tables, allowing for the selection of columns, addition of new columns, merging of two tables, and filtering of rows.

```scala
    <query> ::=  <table>
        | Select <column_list> <query>          
        | NewCol <column_name> <query>         
        | Merge <column_name> <query> <query>   
        | Filter <filter_cond> <query>
```

- **Atomic Query** (`<table>`): The base table on which the query is executed. Starting point for applying further operations on the current offered table.
- **Select** (`Select <column_list> <query>`): Selects a list of columns from the given table. Fails if any of the specified columns do not exist in the table.
- **New Column** (`NewCol <column_name> <query>`): Adds a new column with a specified name and default value to a table. Only successful if the column can be added to both tables involved in the query.
- **Merge** (`Merge <column_name> <query> <query>`): Combines two tables based on a common key column. The operation is possible only if the key column exists in both tables.
- **Filter** (`Filter <filter_cond> <query>`): Applies a filter condition to the rows of the table, retaining only those rows that meet the condition.

## Query

**Flexible and Extensible**: Provides an easy integration of processing functions over the `Option[Table]`, simplifying complex data tasks and establishing a framework for incorporating new features as needed.

- **Select**: Extract data by column names.
- **Filter**: Keep rows that meet specified conditions.
- **Merge**: Join two tables using a shared key column, resolving overlaps and filling missing entries.
- **NewCol**: Introduce a new column filled with a default value across all rows.

**Query Language** operations that allows for sequential and combinable transformations on tables, enabling complex tasks to be executed in an intuitive manner on different sets of csv data files.
