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
package de.griffel.confluence.plugins.plantuml;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Supported SpaceGraph Macro parameters.
 */
public final class DatabaseStructureMacroParams {

   private final int DEFAULT_NODE_FONTSIZE = 9;

   public enum Param {

      datasource,
      schemaName,
      tableTypes,
      tableNameFilter,
      columnNameFilter,
      tableNameRegEx,
      columnNameRegEx,
      nodeFontsize,
      showIndexes,
      showColumns,
      additional,
      debug;
   }

   private final Map<String, String> params;

   public DatabaseStructureMacroParams(Map<String, String> params) {
      this.params = params;
   }

   public String getDatasource() {
      return get(Param.datasource);
   }

   public String getSchemaName() {
      return get(Param.schemaName);
   }

   public List<String> getTableTypes() {
      final String attributes = get(Param.tableTypes);
      if (attributes == null) {
         return Arrays.asList("TABLE");
      } else {
         return Arrays.asList(attributes.trim().split(" *, *"));
      }
   }

   public String getTableNameFilter() {
      return get(Param.tableNameFilter);
   }

   public String getColumnNameFilter() {
      return get(Param.columnNameFilter);
   }

   public String getTableNameRegEx() {
      return get(Param.tableNameRegEx);
   }

   public String getColumnNameRegEx() {
      return get(Param.columnNameRegEx);
   }

   public int getNodeFontsize() {
      try {
         return Integer.parseInt(get(Param.nodeFontsize));
      } catch (NumberFormatException e) {
         return DEFAULT_NODE_FONTSIZE;
      }
   }

   public boolean isShowColumns() {
      final String showColumns = get(Param.showColumns);
      return showColumns != null ? Boolean.valueOf(showColumns) : true;
   }

   public boolean isShowIndexes() {
      final String showIndexes = get(Param.showIndexes);
      return showIndexes != null ? Boolean.valueOf(showIndexes) : false;
   }

   public String getAdditional() {
      String additional = get(Param.additional);
      return additional != null ? additional : "";
   }

   public boolean isDebug() {
      final String debug = get(Param.debug);
      return debug != null ? Boolean.valueOf(debug) : false;
   }

   @Override
   public String toString() {
      return "DatabaseStructureParams [_params=" + params + "]";
   }

   private String get(Param param) {
      return params.get(param.name());
   }

}
