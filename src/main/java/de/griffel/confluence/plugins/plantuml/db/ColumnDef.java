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

/**
 * DAO for columns of a database table.
 */
public class ColumnDef extends BaseDef {

   private final String columnName;
   private final String typeName;
   private final int columnSize;
   private final int decimalDigits;
   private final int nullable;
   private final String comment;
   private final String defaultValue;

   /**
    * Create object.
    *
    * @param tc Table catalog
    * @param ts Table schema
    * @param tn Table name
    * @param cn Column name
    * @param tyn Name of datatype
    * @param cs Size of column
    * @param dd Number of decimal digits
    * @param n Nullable
    * @param c Comment
    * @param dv Default Value
    */
   public ColumnDef(String tc, String ts, String tn, String cn, String tyn, int cs, int dd, int n, String c, String dv) {
      tableCatalog = tc;
      tableSchema = ts;
      tableName = tn;
      columnName = cn;
      typeName = tyn;
      columnSize = cs;
      decimalDigits = dd;
      nullable = n;
      comment = c;
      defaultValue = dv;
   }

   /**
    * Returns full qualified column name.
    *
    * @return Full qualified column name
    */
   public String getColumnId() {
      return getTableId() + "." + getColumnName();
   }

   /**
    * Returns column name.
    *
    * @return Column name
    */
   public String getColumnName() {
      return columnName;
   }

   /**
    * Returns name of data type.
    *
    * @return Data type name
    */
   public String getTypeName() {
      return typeName;
   }

   /**
    * Returns size of column.
    *
    * @return Column size
    */
   public int getColumnSize() {
      return columnSize;
   }

   /**
    * Returns number of decimal digits.
    *
    * @return Number of decimal digits.
    */
   public int getDecimalDigits() {
      return decimalDigits;
   }

   /**
    * Is column nullable.
    *
    * @return <tt>true</tt> if column is nullable
    */
   public int getNullable() {
      return nullable;
   }

   /**
    * Returns comment.
    * @return Comment
    */
   public String getComment() {
      return comment;
   }

   /**
    * Default value of this column.
    * @return  Default value of this column.
    */
   public String getDefaultValue() {
      return defaultValue;
   }
}
