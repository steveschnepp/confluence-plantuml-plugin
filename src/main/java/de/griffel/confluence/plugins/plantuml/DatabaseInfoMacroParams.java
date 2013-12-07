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
 * Supported DatabaseInfo Macro parameters.
 */
public final class DatabaseInfoMacroParams {

   // all the attributes that can be shown
   private final List<String> allJdbc4MetaDataAttributes;

   public enum Param {

      datasources,
      attributes
   }

   private final Map<String, String> params;

   public DatabaseInfoMacroParams(Map<String, String> params) {
      this.allJdbc4MetaDataAttributes = Arrays.asList(DatasourceHelper.CATALOGS, "CatalogSeparator", "CatalogTerm", "DatabaseMajorVersion",
              "DatabaseMinorVersion", "DatabaseProductName", "DatabaseProductVersion", "DefaultTransactionIsolation",
              "DriverMajorVersion", "DriverMinorVersion", "DriverName", "DriverVersion", "ExtraNameCharacters",
              "IdentifierQuoteString", "JDBCMajorVersion", "JDBCMinorVersion", "MaxBinaryLiteralLength", "MaxCatalogNameLength",
              "MaxCharLiteralLength", "MaxColumnNameLength", "MaxColumnsInGroupBy", "MaxColumnsInIndex", "MaxColumnsInOrderBy",
              "MaxColumnsInSelect", "MaxColumnsInTable", "MaxConnections", "MaxCursorNameLength", "MaxIndexLength",
              "MaxProcedureNameLength", "MaxRowSize", "MaxSchemaNameLength", "MaxStatementLength", "MaxStatements",
              "MaxTableNameLength", "MaxTablesInSelect", "MaxUserNameLength", "NumericFunctions", "ProcedureTerm",
              "ResultSetHoldability", "SQLKeywords", "SchemaTerm", "SearchStringEscape", "StringFunctions", "SystemFunctions",
              "TimeDateFunctions", DatasourceHelper.TABLE_TYPES, "URL", "UserName");
      this.params = params;
   }

   public List<String> getDatasources() {
      final String datasources = get(Param.datasources);
      if (datasources == null) {
         return DatasourceHelper.listAvailableDataSources();
      } else {
         return Arrays.asList(datasources.trim().split(" *, *"));
      }
   }

   public List<String> getAttributes() {
      final String attributes = get(Param.attributes);
      if (attributes == null) {
         return allJdbc4MetaDataAttributes;
      } else {
         return Arrays.asList(attributes.trim().split(" *, *"));
      }
   }

   public List<String> getAllAvailableAttributes() {
      return allJdbc4MetaDataAttributes;
   }

   @Override
   public String toString() {
      return "DatabaseInfoMacroParams [_params=" + params + "]";
   }

   private String get(Param param) {
      return params.get(param.name());
   }

}
