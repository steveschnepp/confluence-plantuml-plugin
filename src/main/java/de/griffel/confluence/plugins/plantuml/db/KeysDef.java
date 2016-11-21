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
 * DAO for primary/foreign keys of a database table.
 */
public class KeysDef {

   private final String tableCatalogPk;
   private final String tableSchemaPk;
   private final String tableNamePk;
   private final String columnNamePk;
   private final String tableCatalogFk;
   private final String tableSchemaFk;
   private final String tableNameFk;
   private final String columnNameFk;

   /**
    * Create new object
    *
    * @param tcp Table catalog of primary key
    * @param tsp Table schema of primary key
    * @param tnp Table name of primary key
    * @param cnp Column name of primary key
    * @param tcf Table catalog of foreign key
    * @param tsf Table schema of foreign key
    * @param tnf Table name of foreign key
    * @param cnf Column name of foreign key
    */
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

   /**
    * Returns full qualified name of primary key on table level.
    *
    * @return Full qualified name of primary key on table level
    */
   public String getPkTableId() {
      return getTableCatalogPk() + "." + getTableSchemaPk() + "." + getTableNamePk();
   }

   /**
    * Returns full qualified name of foreign key on table level.
    *
    * @return Full qualified name of foreign on table level
    */
   public String getFkTableId() {
      return getTableCatalogFk() + "." + getTableSchemaFk() + "." + getTableNameFk();
   }

   /**
    * Returns full qualified name of primary key on column level.
    *
    * @return Full qualified name of primary key on column level
    */
   public String getPkColumnId() {
      return getPkTableId() + "." + getColumnNamePk();
   }

   /**
    * Full qualified name of foreign key on column level
    *
    * @return Full qualified name of foreign key on column level
    */
   public String getFkColumnId() {
      return getFkTableId() + "." + getColumnNameFk();
   }

   /**
    * Returns primary+foreign key on table level.
    *
    * @return Primary+foreign key name on table level
    */
   public String getKeysTableId() {
      return getPkTableId() + " -> " + getFkTableId();
   }

   /**
    * Returns primary+foreign key on column level.
    *
    * @return Primary+foreign key on column level
    */
   public String getKeysColumnId() {
      return getPkColumnId() + " -> " + getFkColumnId();
   }

   /**
    * Returns table catalog of primary key.
    *
    * @return Table catalog of primary key
    */
   public String getTableCatalogPk() {
      return tableCatalogPk;
   }

   /**
    * Returns table schema of primary key.
    *
    * @return table schema of primary key
    */
   public String getTableSchemaPk() {
      return tableSchemaPk;
   }

   /**
    * Returns table name of primary key
    *
    * @return Table name of primary key
    */
   public String getTableNamePk() {
      return tableNamePk;
   }

   /**
    * Returns column name of primary key
    *
    * @return Column name of primary key
    */
   public String getColumnNamePk() {
      return columnNamePk;
   }

   /**
    * Returns table catalog of foreign key.
    *
    * @return Table catalog of foreign key
    */
   public String getTableCatalogFk() {
      return tableCatalogFk;
   }

   /**
    * Returns table schema of foreign key.
    *
    * @return table schema of foreign key
    */
   public String getTableSchemaFk() {
      return tableSchemaFk;
   }

   /**
    * Returns table name of foreign key
    *
    * @return Table name of foreign key
    */
   public String getTableNameFk() {
      return tableNameFk;
   }

   /**
    * Returns column name of foreign key
    *
    * @return Column name of foreign key
    */
   public String getColumnNameFk() {
      return columnNameFk;
   }

}
