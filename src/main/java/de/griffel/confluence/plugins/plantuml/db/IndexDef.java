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
 *
 * @author chris
 */
public class IndexDef extends BaseDef {
   // boolean nonUnique; // Can index values be non-unique. false when TYPE is tableIndexStatistic
   public String indexQualifier; // index catalog (may be null); null when TYPE is tableIndexStatistic
   public String indexName; // null when TYPE is tableIndexStatistic
   // public short type; // tableIndexStatistic,  tableIndexClustered, tableIndexHashed, tableIndexOther
   public short ordinalPosition; // column sequence number within index; zero when TYPE is tableIndexStatistic
   public String columnName; // null when TYPE is tableIndexStatistic
   // String columnSort; // "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
   // int cardinality; // When TYPE is tableIndexStatistic, then this is the number of rows in the table; otherwise, it is the number of unique values in the index.
   // int pages; // When TYPE is tableIndexStatisic then this is the number of pages used for the table, otherwise it is the number of pages used for the current index.
   // String filterCondition; // Filter condition, if any. (may be null)

   public IndexDef(String tc, String ts, String tn, String iq, String in, short op, String cn) {
      tableCatalog = tc;
      tableSchema = ts;
      tableName = tn;
      indexQualifier = iq;
      indexName = in;
      ordinalPosition = op;
      columnName = cn;
   }

   public String getColumnId() {
      return getTableId() + "." + columnName;
   }

   public String getIndexId() {
      return indexQualifier + "." + indexName + "." + ordinalPosition + ": " + getColumnId();
   }
}
