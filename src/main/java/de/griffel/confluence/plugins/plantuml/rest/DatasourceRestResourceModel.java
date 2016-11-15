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
package de.griffel.confluence.plugins.plantuml.rest;

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Datasource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasourceRestResourceModel {

   @XmlElement(name = "Catalogs")
   public String catalogs;

   @XmlElement(name = "CatalogSeparator")
   public String catalogSeparator;

   @XmlElement(name = "CatalogTerm")
   public String catalogTerm;

   @XmlElement(name = "DatabaseMajorVersion")
   public String databaseMajorVersion;

   @XmlElement(name = "DatabaseMinorVersion")
   public String databaseMinorVersion;

   @XmlElement(name = "DatabaseProductName")
   public String databaseProductName;

   @XmlElement(name = "DatabaseProductVersion")
   public String databaseProductVersion;

   @XmlElement(name = "DefaultTransactionIsolation")
   public String defaultTransactionIsolation;

   @XmlElement(name = "DriverMajorVersion")
   public String driverMajorVersion;

   @XmlElement(name = "DriverMinorVersion")
   public String driverMinorVersion;

   @XmlElement(name = "DriverName")
   public String driverName;

   @XmlElement(name = "DriverVersion")
   public String driverVersion;

   @XmlElement(name = "ExtraNameCharacters")
   public String extraNameCharacters;

   @XmlElement(name = "IdentifierQuoteString")
   public String identifierQuoteString;

   @XmlElement(name = "JDBCMajorVersion")
   public String jDBCMajorVersion;

   @XmlElement(name = "JDBCMinorVersion")
   public String jDBCMinorVersion;

   @XmlElement(name = "MaxBinaryLiteralLength")
   public String maxBinaryLiteralLength;

   @XmlElement(name = "MaxCatalogNameLength")
   public String maxCatalogNameLength;

   @XmlElement(name = "MaxCharLiteralLength")
   public String maxCharLiteralLength;

   @XmlElement(name = "MaxColumnNameLength")
   public String maxColumnNameLength;

   @XmlElement(name = "MaxColumnsInGroupBy")
   public String maxColumnsInGroupBy;

   @XmlElement(name = "MaxColumnsInIndex")
   public String maxColumnsInIndex;

   @XmlElement(name = "MaxColumnsInOrderBy")
   public String maxColumnsInOrderBy;

   @XmlElement(name = "MaxColumnsInSelect")
   public String maxColumnsInSelect;

   @XmlElement(name = "MaxColumnsInTable")
   public String maxColumnsInTable;

   @XmlElement(name = "MaxConnections")
   public String maxConnections;

   @XmlElement(name = "MaxCursorNameLength")
   public String maxCursorNameLength;

   @XmlElement(name = "MaxIndexLength")
   public String maxIndexLength;

   @XmlElement(name = "MaxProcedureNameLength")
   public String maxProcedureNameLength;

   @XmlElement(name = "MaxRowSize")
   public String maxRowSize;

   @XmlElement(name = "MaxSchemaNameLength")
   public String maxSchemaNameLength;

   @XmlElement(name = "MaxStatementLength")
   public String maxStatementLength;

   @XmlElement(name = "MaxStatements")
   public String maxStatements;

   @XmlElement(name = "MaxTableNameLength")
   public String maxTableNameLength;

   @XmlElement(name = "MaxTablesInSelect")
   public String maxTablesInSelect;

   @XmlElement(name = "MaxUserNameLength")
   public String maxUserNameLength;

   @XmlElement(name = "NumericFunctions")
   public String numericFunctions;

   @XmlElement(name = "ProcedureTerm")
   public String procedureTerm;

   @XmlElement(name = "ResultSetHoldability")
   public String resultSetHoldability;

   @XmlElement(name = "SQLKeywords")
   public String sQLKeywords;

   @XmlElement(name = "SchemaTerm")
   public String schemaTerm;

   @XmlElement(name = "SearchStringEscape")
   public String searchStringEscape;

   @XmlElement(name = "StringFunctions")
   public String stringFunctions;

   @XmlElement(name = "SystemFunctions")
   public String systemFunctions;

   @XmlElement(name = "TableTypes")
   public String tableTypes;

   @XmlElement(name = "TimeDateFunctions")
   public String timeDateFunctions;

   @XmlElement(name = "URL")
   public String url;

   @XmlElement(name = "UserName")
   public String userName;

   public DatasourceRestResourceModel() {
   }

   public DatasourceRestResourceModel(Map<String, String> datasource) {
      if (datasource != null) {
         this.catalogs = datasource.get("Catalogs");
         this.catalogSeparator = datasource.get("CatalogSeparator");
         this.catalogTerm = datasource.get("CatalogTerm");
         this.databaseMajorVersion = datasource.get("DatabaseMajorVersion");
         this.databaseMinorVersion = datasource.get("DatabaseMinorVersion");
         this.databaseProductName = datasource.get("DatabaseProductName");
         this.databaseProductVersion = datasource.get("DatabaseProductVersion");
         this.defaultTransactionIsolation = datasource.get("DefaultTransactionIsolation");
         this.driverMajorVersion = datasource.get("DriverMajorVersion");
         this.driverMinorVersion = datasource.get("DriverMinorVersion");
         this.driverName = datasource.get("DriverName");
         this.driverVersion = datasource.get("DriverVersion");
         this.extraNameCharacters = datasource.get("ExtraNameCharacters");
         this.identifierQuoteString = datasource.get("IdentifierQuoteString");
         this.jDBCMajorVersion = datasource.get("JDBCMajorVersion");
         this.jDBCMinorVersion = datasource.get("JDBCMinorVersion");
         this.maxBinaryLiteralLength = datasource.get("MaxBinaryLiteralLength");
         this.maxCatalogNameLength = datasource.get("MaxCatalogNameLength");
         this.maxCharLiteralLength = datasource.get("MaxCharLiteralLength");
         this.maxColumnNameLength = datasource.get("MaxColumnNameLength");
         this.maxColumnsInGroupBy = datasource.get("MaxColumnsInGroupBy");
         this.maxColumnsInIndex = datasource.get("MaxColumnsInIndex");
         this.maxColumnsInOrderBy = datasource.get("MaxColumnsInOrderBy");
         this.maxColumnsInSelect = datasource.get("MaxColumnsInSelect");
         this.maxColumnsInTable = datasource.get("MaxColumnsInTable");
         this.maxConnections = datasource.get("MaxConnections");
         this.maxCursorNameLength = datasource.get("MaxCursorNameLength");
         this.maxIndexLength = datasource.get("MaxIndexLength");
         this.maxProcedureNameLength = datasource.get("MaxProcedureNameLength");
         this.maxRowSize = datasource.get("MaxRowSize");
         this.maxSchemaNameLength = datasource.get("MaxSchemaNameLength");
         this.maxStatementLength = datasource.get("MaxStatementLength");
         this.maxStatements = datasource.get("MaxStatements");
         this.maxTableNameLength = datasource.get("MaxTableNameLength");
         this.maxTablesInSelect = datasource.get("MaxTablesInSelect");
         this.maxUserNameLength = datasource.get("MaxUserNameLength");
         this.numericFunctions = datasource.get("NumericFunctions");
         this.procedureTerm = datasource.get("ProcedureTerm");
         this.resultSetHoldability = datasource.get("ResultSetHoldability");
         this.sQLKeywords = datasource.get("SQLKeywords");
         this.schemaTerm = datasource.get("SchemaTerm");
         this.searchStringEscape = datasource.get("SearchStringEscape");
         this.stringFunctions = datasource.get("StringFunctions");
         this.systemFunctions = datasource.get("SystemFunctions");
         this.tableTypes = datasource.get("TableTypes");
         this.timeDateFunctions = datasource.get("TimeDateFunctions");
         this.url = datasource.get("URL");
         this.userName = datasource.get("UserName");
      }
   }
}
