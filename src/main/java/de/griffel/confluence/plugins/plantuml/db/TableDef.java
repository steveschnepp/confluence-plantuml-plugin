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

import java.util.LinkedList;
import java.util.List;

public class TableDef extends BaseDef {

   private final String tableType;
   private final List<ColumnDef> columns;
   private List<IndexDef> indices;

   public TableDef(String tc, String ts, String tn, String tt) {
      tableCatalog = tc;
      tableSchema = ts;
      tableName = tn;
      tableType = tt;
      columns = new LinkedList<ColumnDef>();
   }

   public String display() {
      return tableCatalog + "." + tableSchema + "." + tableName;
   }

   public String getTableType() {
      return tableType;
   }

   public List<ColumnDef> getColumns() {
      return columns;
   }

   public List<IndexDef> getIndices() {
      return indices;
   }

   public void setIndices(List<IndexDef> indices) {
      this.indices = indices;
   }

}
