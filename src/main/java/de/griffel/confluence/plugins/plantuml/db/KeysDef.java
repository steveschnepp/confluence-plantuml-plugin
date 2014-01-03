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

public class KeysDef {

   private final String tableCatalogPk;
   private final String tableSchemaPk;
   private final String tableNamePk;
   private final String columnNamePk;
   private final String tableCatalogFk;
   private final String tableSchemaFk;
   private final String tableNameFk;
   private final String columnNameFk;

   public KeysDef(String tcp, String tsp, String tnp, String cnp, String tcf, String tsf, String tnf, String cnf) {
      tableCatalogPk = tcp;
      tableSchemaPk = tsp;
      tableNamePk = tnp;
      columnNamePk = cnp;
      tableCatalogFk = tcf;
      tableSchemaFk = tsf;
      tableNameFk = tnf;
      columnNameFk = cnf;
   }

   public String getPkTableId() {
      return getTableCatalogPk() + "." + getTableSchemaPk() + "." + getTableNamePk();
   }

   public String getFkTableId() {
      return getTableCatalogFk() + "." + getTableSchemaFk() + "." + getTableNameFk();
   }

   public String getPkColumnId() {
      return getPkTableId() + "." + getColumnNamePk();
   }

   public String getFkColumnId() {
      return getFkTableId() + "." + getColumnNameFk();
   }

   public String getKeysTableId() {
      return getPkTableId() + " -> " + getFkTableId();
   }

   public String getKeysColumnId() {
      return getPkColumnId() + " -> " + getFkColumnId();
   }

   public String getTableCatalogPk() {
      return tableCatalogPk;
   }

   public String getTableSchemaPk() {
      return tableSchemaPk;
   }

   public String getTableNamePk() {
      return tableNamePk;
   }

   public String getColumnNamePk() {
      return columnNamePk;
   }

   public String getTableCatalogFk() {
      return tableCatalogFk;
   }

   public String getTableSchemaFk() {
      return tableSchemaFk;
   }

   public String getTableNameFk() {
      return tableNameFk;
   }

   public String getColumnNameFk() {
      return columnNameFk;
   }

}
