/*
 * Copyright (C) 2011 Michael Griffel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This distribution includes other third-party libraries.
 * These libraries and their corresponding licenses (where different
 * from the GNU General Public License) are enumerated below.
 *
 * PlantUML is a Open-Source tool in Java to draw UML Diagram.
 * The software is developed by Arnaud Roques at
 * http://plantuml.sourceforge.org.
 */
package de.griffel.confluence.plugins.plantuml.db;

public class ColumnDef extends BaseDef {

   public String columnName;
   // public int dataType;        // SQL type from java.sql.Types
   public String typeName;        // Data source dependent type name, for a UDT the type name is fully qualified
   public int columnSize;
   // int bufferLength;    // not used
   public int decimalDigits;      // the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
   // public int numPrecRadix;       //  Radix (typically either 10 or 2)
   public int nullable;           // is NULL allowed. columnNoNulls - might not allow NULL values,  columnNullable - definitely allows NULL values, columnNullableUnknown - nullability unknown
   // public String remarks;         // comment describing column (may be null)
   // public String columnDefaultValue; // default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
   // int sqlDataType;     // unused
   // int sqlDateTimeSub;  // unused
   // public int charOctetLenght;    // for char types the maximum number of bytes in the column
   // public int ordinalPosition;    // index of column in table (starting at 1)
   // String isNullable;   // ISO rules are used to determine the nullability for a column.  YES, NO, empty string (if the nullability is unknown)
   // String scopeCatalog; // catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
   // String scopeSchema;  // schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
   // String scopeTable;   // table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
   // short sourceDataType;     //  source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
   // String isAutoincrement;   // Indicates whether this column is auto incremented: YES, NO, empty string (if it cannot be determined)
   // String isGeneratedColumn; // Indicates whether this is a generated column: YES, NO, empty string (if it cannot be determined)

   public ColumnDef(String tc, String ts, String tn, String cn, String tyn, int cs, int dd, int n) {
      tableCatalog = tc;
      tableSchema = ts;
      tableName = tn;
      columnName = cn;
      typeName = tyn;
      columnSize = cs;
      decimalDigits = dd;
      nullable = n;
   }

   public String getColumnId() {
      return getTableId() + "." + columnName;
   }
}
