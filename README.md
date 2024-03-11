# Query Language

## Description

CSV table operations using custom-defined data types. The objective *is not just to perform operations on tables* but to provide a **foundation for a Query Language** that allows complex data manipulations. This Query Language offers the ability to execute simple operations on tables, thus opening up possibilities both flexible and easy to expand with new functionalities.

### Table Structure

The `Table` data structure is built to handle tabular data, consisting of `String` cells arranged in rows and columns. Each column in a table has a name to make it easier to work with the data. We use two main ways to represent a row in this structure:

- **Line**: This is just a list of the values in a row, put in order. It's a simple way to look at row data when you don't need to know about the column names.
- **Row**: This takes the form of a map where each column name is linked to its value in the row. It's useful for when you need to do operations that depend on knowing which data belongs to which column.

```scala
    // Row contains the column name and the value
    type Row = Map[String, String]
    // Line is just a row without column names
    type Line = List[String]
```

These two row representations give different ways to work with table data, making the `Table` structure flexible for various tasks. Use `Line` for straightforward tasks and `Row` when you need to do more detailed work that involves column names.

### Query Structure

The provided grammar defines a query language for operations on tables, allowing for the selection of columns, addition of new columns, merging of two tables, and filtering of rows. Here's an explanation in English:

- **Atomic Query** (`<table>`): The base table on which the query is executed. Starting point for applying further operations.
- **Select** (`Select <column_list> <query>`): Selects a list of columns from the given table. Fails if any of the specified columns do not exist in the table.
- **New Column** (`NewCol <column_name> <query>`): Adds a new column with a specified name and default value to a table. Only successful if the column can be added to both tables involved in the query.
- **Merge** (`Merge <column_name> <query> <query>`): Combines two tables based on a common key column. The operation is possible only if the key column exists in both tables.
- **Filter** (`Filter <filter_cond> <query>`): Applies a filter condition to the rows of the table, retaining only those rows that meet the condition.

## Table

Robust toolkit for simple data table manipulation and CSV file handling, articulated through the following enhanced features:

- **Parsing and Display (`apply`):** Parse input and generate output in CSV format, data exchange and visualization.
- **Column Selection (`select`)**: Extract specific columns from a table based on user input. It returns `None` when specified columns do not exist.
- **Column Addition (`newCol`)**: Addition of a new column to the table, populating it with a default value across all rows. This feature is essential for data enrichment and preparing datasets for further analysis.
- **Row Filtering (`filter`)**: Applies user-defined conditions to table rows, retaining only those that meet the criteria. The `filter` function extends to logical operations, supporting `&&` (AND), `||` (OR), and direct column value comparisons. It handles cases involving non-existent columns by returning `None`.
- **Table Merging (`merge`)**: Merges two tables based on a shared key column. It uniquely handles overlapping values by concatenating them with (`;`), preserving both datasets' integrity, ensuring no data loss. For unmatched rows, it fills missing columns with empty strings, ensuring the merged table's completeness. This approach highlights our commitment to data integrity, guaranteeing no loss of valuable information.

## Query

- **Flexible and Extensible**: Allows for the seamless combination of the above processing functions. This not only simplifies complex data manipulation tasks but also provides a framework for extending the toolkit with new features as needed.

- **Select**: Extract data by column names.
- **Filter**: Keep rows that meet specified conditions.
- **Merge**: Join two tables using a shared key column, resolving overlaps and filling missing entries.
- **NewCol**: Introduce a new column filled with a default value across all rows.

These operations are part of a Query Language that allows for sequential and combinable transformations on tables, enabling complex data manipulation tasks to be executed in a straightforward and intuitive manner.

## Query Language Examples

- **Creating a New Column**: Add a column indicating the language type with a default value "Yes".
- **Merging Tables**: Combine tables by programming language names.
- **Filtering**: Retain only languages originally intended for "Application" that are "concurrent".
- **Selecting Columns**: Choose specific columns like "Language", "Object-Oriented", and "Functional" from the processed table.

This toolkit provides a solid foundation for handling and transforming CSV data through a simple yet powerful query language, making it an essential resource for data manipulation tasks.
