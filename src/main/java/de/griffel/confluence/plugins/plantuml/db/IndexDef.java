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
 * DAO for indices of a database table.
 */
public class IndexDef extends BaseDef {

   private final String indexQualifier;
   private final String indexName;
   private final short ordinalPosition;
   private final String columnName;

   /**
    * Create object.
    * @param tc Table catalog
    * @param ts Table schema
    * @param tn Table name
    * @param iq Index qualifier
    * @param in Index name
    * @param op Ordinal position
    * @param cn Column name
    */
   public IndexDef(String tc, String ts, String tn, String iq, String in, short op, String cn) {
      tableCatalog = tc;
      tableSchema = ts;
      tableName = tn;
      indexQualifier = iq;
      indexName = in;
      ordinalPosition = op;
      columnName = cn;
   }

   /**
    * Returns full qualified name on column level
    * @return Full qualified name on column level
    */
   public String getColumnId() {
      return getTableId() + "." + getColumnName();
   }

   /**
    * Returns full qualified name
    * @return Full qualified name
    */
   public String getIndexId() {
      return getIndexQualifier() + "." + getIndexName() + "." + getOrdinalPosition() + ": " + getColumnId();
   }

   /**
    * Returns index qualifier.
    * @return Index qualifier
    */
   public String getIndexQualifier() {
      return indexQualifier;
   }

   /**
    * Returns name of index.
    * @return Index name
    */
   public String getIndexName() {
      return indexName;
   }

   /**
    * Returns ordinal position.
    * @return Ordinal position.
    */
   public short getOrdinalPosition() {
      return ordinalPosition;
   }

   /**
    * Returns column name
    * @return Column name
    */
   public String getColumnName() {
      return columnName;
   }
}
