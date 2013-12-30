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

import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import de.griffel.confluence.plugins.plantuml.db.ColumnDef;
import de.griffel.confluence.plugins.plantuml.db.IndexDef;
import de.griffel.confluence.plugins.plantuml.db.KeysDef;
import de.griffel.confluence.plugins.plantuml.db.TableDef;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.sourceforge.plantuml.core.DiagramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the abstract implementation class of the {database-structure} macro.
 */
abstract class AbstractDatabaseStructureMacroImpl {

   private static final Logger log = LoggerFactory.getLogger(AbstractDatabaseStructureMacroImpl.class);
   protected DatabaseStructureMacroParams _macroParams;
   protected String     _errorMessage;
   protected Connection _con;

   public String execute(Map<String, String> params, String dotString, RenderContext context) throws MacroException {
      final DatabaseStructureMacroParams macroParams = new DatabaseStructureMacroParams(params);

      params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
      if (macroParams.isDebug()) {
         params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.TRUE.toString());
      }

      return executePlantUmlMacro(params, dotString, context);
   }

   protected abstract String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
           throws MacroException;

   /**
    * Create dot-String
    */
   public String createDotForDatabaseStructure(Map<String, String> params, PageContext pageContext, SpaceManager spaceManager,
           PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager,
           ContentPropertyManager contentPropertyManager) {

      _macroParams = new DatabaseStructureMacroParams(params);
      final DatabaseMetaData dbmd = openDatabaseMetaData(_macroParams.getDatasource());

      try {
         Map<String, TableDef> tables = getTables(dbmd);
         linkColumnsWithTables(tables, filterColumnsByName(getColumns(dbmd), _macroParams.getColumnNameRegEx()));
         List<ColumnDef> columns = null; // free resources
         tables = filterTablesByName(filterTablesByType(tables, _macroParams.getTableTypes()), _macroParams.getTableNameRegEx());
         for (String key : tables.keySet()) {
            addIndexDetails(dbmd, tables.get(key));
         }
         return buildDot(tables, columns, reduceToTableReferences(getForeignKeys(dbmd)));
      } finally {
         closeDatabaseMetaData(dbmd); // free resources
      }
   }

   private String buildDot(Map<String, TableDef> tables, List<ColumnDef> columns, List<KeysDef> keys) {
      final StringBuilder sb = new StringBuilder("digraph g {\n");
      sb.append("rankdir=LR;\n");
      sb.append("edge [arrowsize=\"0.8\"];");
      sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"lightyellow\", color=\"#bc3b59\", fontname=\"Verdana\", ratio=\"compress\", fontsize=\"");
      sb.append(_macroParams.getNodeFontsize()).append("\"];\n");
      sb.append(_macroParams.getAdditional()).append("\n");

      if (_errorMessage != null) {
         sb.append("\"").append(_errorMessage).append("\"");
      } else {
         for (TableDef table : tables.values()) {
            sb.append(cleanNodeId(table.getTableId())).append(" [ label = \"");
            buildTableName(sb, table);
            buildColumns(sb, table);
            buildIndexesInTables(sb, table);
            sb.append("\"\nshape=\"record\"];\n");
            buildIndexRelations(sb, table, tables);
         }
         buildTableRelations(sb, keys, tables);
      }
      sb.append("}\n");

      log.info(sb.toString());
      return sb.toString();
   }

   /**
    * Replaces characters in names which are not allowed as NodeId.
    * @param s String to be used as NodeId
    * @return Cleaned string
    */
   private String cleanNodeId(String s) {
      return s.replaceAll("\\.", "_").replaceAll("\\$", "_S_");
   }

   private void buildTableName(StringBuilder sb, TableDef table) {
      if (_macroParams.getTableTypes().size() != 1) {
        sb.append("«").append(table.tableType).append("»\\n"); // show type if multiple are possible
      }

      if (_macroParams.getSchemaName() == null) {
         sb.append(table.tableSchema).append(".");
      }

      sb.append(table.tableName);
   }

   private void buildColumns(final StringBuilder sb, TableDef table) {
      if (_macroParams.isShowColumns() && table.columns.size() > 0) {
         sb.append(" | {");
         for (ColumnDef c : table.columns) {
            sb.append("+ ").append(c.columnName).append(": ").append(c.typeName);
            if (c.typeName.contains("char") || c.typeName.contains("CHAR")) {
               sb.append("(").append(c.columnSize).append(")");
            }
            if (c.typeName.startsWith("num") || c.typeName.startsWith("NUM")) {
               sb.append("(").append(c.columnSize).append(",").append(c.decimalDigits).append(")");
            }
            if (c.nullable == 0) {
               sb.append(" \\{not null\\}");
            }
            sb.append("\\l");
         }
         sb.append("}");
      }
   }

   private void buildIndexesInTables(final StringBuilder sb, TableDef table) {
      if (_macroParams.isShowIndexes() && table.indices.size() > 0) {
         boolean first = true;
         sb.append(" | {");
         for (IndexDef ix : table.indices) { // data are ordered
            if (ix.ordinalPosition == 1) {
               if (!first) {
                  sb.append("\\l");
               } else {
                  first = false;
               }
               sb.append(ix.indexName).append(":");
            }
            if (ix.columnName != null) { // not table statistics
               sb.append(" ").append(ix.columnName.replaceAll("\"", ""));
            }
         }
         sb.append("\\l}");
      }
   }

   private void buildIndexRelations(final StringBuilder sb, TableDef currentTable, Map<String, TableDef> tables) {
      for (IndexDef ix : currentTable.indices) {
         if (ix.ordinalPosition == 1){
            for (TableDef referencedTable : tables.values()) {
               if (ix.indexName.equals(referencedTable.tableName)) {
                  sb.append(cleanNodeId(currentTable.getTableId()))
                    .append(" -> ")
                    .append(cleanNodeId(referencedTable.getTableId()))
                    .append(" [arrowhead=none, color=\"#999999\"];\n");
               }
            }
         }
      }
   }

   private void buildTableRelations(final StringBuilder sb, List<KeysDef> keys, Map<String, TableDef> tables) {
      for (KeysDef key : keys) {
         if (tables.containsKey(key.getFkTableId()) && tables.containsKey(key.getPkTableId())) {
            sb.append(cleanNodeId(key.getPkTableId()))
              .append(" -> ")
              .append(cleanNodeId(key.getFkTableId()))
              .append("\n");
         }
      }
   }

   private List<KeysDef> reduceToTableReferences(List<KeysDef> keys) {
      final List<KeysDef> result = new ArrayList<KeysDef>();
      final Map<String, String> tmp = new HashMap<String, String>();
      for (KeysDef k : keys) {
         final String relTables = k.getKeysTableId();
         if (!tmp.containsKey(relTables)) {
            tmp.put(relTables, relTables);
            result.add(k);
         }
      }
      return result;
   }

   private Map<String, TableDef> filterTablesByType(Map<String, TableDef> tables, List<String> tableTypes) {
      if (tableTypes == null || tableTypes.isEmpty()) {
         return tables;
      }

      final Map<String, TableDef> result = new HashMap<String, TableDef>();
      for (String key : tables.keySet()) {
         final TableDef t = tables.get(key);
         if (t.tableType != null && tableTypes.contains(t.tableType)) {
            result.put(key, t);
         }
      }
      return result;
   }

   private Map<String, TableDef> filterTablesByName(Map<String, TableDef> tables, String tableNameRegEx) {
      if (tableNameRegEx == null || tableNameRegEx.isEmpty()) {
         return tables;
      }

      final Map<String, TableDef> result = new HashMap<String, TableDef>();
      for (String key : tables.keySet()) {
         final TableDef t = tables.get(key);
         if (t.tableName.matches(tableNameRegEx)) {
            result.put(key, t);
         }
      }
      return result;
   }

   private List<ColumnDef> filterColumnsByName(List<ColumnDef> columns, String columnNameRegEx) {
      if (columnNameRegEx == null || columnNameRegEx.isEmpty()) {
         return columns;
      }

      final List<ColumnDef> result = new LinkedList<ColumnDef>();
      for (ColumnDef c : columns) {
         if (c.columnName.matches(columnNameRegEx)) {
            result.add(c);
         }
      }
      return result;
   }

   private void linkColumnsWithTables(Map<String, TableDef> tables, List<ColumnDef> columns) {
      for (ColumnDef column : columns) {
         tables.get(column.getTableId()).columns.add(column);
      }
   }

   private Map<String, TableDef> getTables(DatabaseMetaData dbmd) {
      final Map<String, TableDef> result = new HashMap<String, TableDef>();

      if (_errorMessage == null) {
         ResultSet rs = null;
         try {
            rs = dbmd.getTables(null, _macroParams.getSchemaName(), _macroParams.getTableNameFilter(), null);
            while (rs.next()) {
               TableDef tmp = new TableDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
               result.put(tmp.getTableId(), tmp);
               log.debug(tmp.display());
            }
         } catch (SQLException e) {
            _errorMessage = e.getMessage();
            log.error("SQLException " + _macroParams.getDatasource() + ": " + _errorMessage);
         } finally {
            try {
               if (rs != null) {
                  rs.close();
               }
            } catch (SQLException e) { /* do nothing */ }
         }
      }
      return result;
   }

   private List<ColumnDef> getColumns(DatabaseMetaData dbmd) {
      final List<ColumnDef> result = new LinkedList<ColumnDef>();

      if (_errorMessage == null && _macroParams.isShowColumns()) {
         ResultSet rs = null;
         try {
            rs = dbmd.getColumns(null, _macroParams.getSchemaName(), _macroParams.getTableNameFilter(), _macroParams.getColumnNameFilter());
            while (rs.next()) {
               ColumnDef tmp = new ColumnDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
                       rs.getString(6), rs.getInt(7), rs.getInt(9), rs.getInt(10), rs.getInt(11),
                       rs.getString(12), rs.getString(13), rs.getInt(16), rs.getInt(17));
               result.add(tmp);
               log.debug(tmp.getColumnId());
            }
         } catch (SQLException e) {
            _errorMessage = e.getMessage();
            log.error("SQLException " + _macroParams.getDatasource() + ": " + _errorMessage);
         } finally {
            try {
               if (rs != null) {
                  rs.close();
               }
            } catch (SQLException e) { /* do nothing */ }
         }
      }
      return result;
   }

   private List<KeysDef> getForeignKeys(DatabaseMetaData dbmd) {
      final List<KeysDef> result = new LinkedList<KeysDef>();

      if (_errorMessage == null) {
         ResultSet rs = null;
         try {
            rs = dbmd.getImportedKeys(null, _macroParams.getSchemaName(), null);
            while (rs.next()) {
               KeysDef tmp = new KeysDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                       rs.getString(6), rs.getString(7), rs.getString(8), rs.getShort(9), rs.getString(12), rs.getString(13));
               result.add(tmp);
               log.debug(tmp.getKeysColumnId());
            }
         } catch (SQLException e) {
            _errorMessage = e.getMessage();
            log.error("SQLException " + _macroParams.getDatasource() + ": " + _errorMessage);
         } finally {
            try {
               if (rs != null) {
                  rs.close();
               }
            } catch (SQLException e) { /* do nothing */ }
         }
      }
      return result;
   }

   private void addIndexDetails(DatabaseMetaData dbmd, TableDef t) {
      final List<IndexDef> result = new LinkedList<IndexDef>();

      if (_errorMessage == null && (_macroParams.isShowIndexes() || _macroParams.getTableTypes().contains("INDEX"))) {
         ResultSet rs = null;
         try {
            rs = dbmd.getIndexInfo(t.tableCatalog, t.tableSchema, t.tableName, false /* also non-unique */, true /* approximate */);

            while (rs.next()) {
               IndexDef tmp = new IndexDef(rs.getString(1), rs.getString(2), rs.getString(3),
                       rs.getString(5), rs.getString(6), rs.getShort(7), rs.getShort(8), rs.getString(9));
               result.add(tmp);
               log.debug(tmp.getIndexId());
            }
         } catch (SQLException e) {
            _errorMessage = e.getMessage();
            log.error("SQLException " + _macroParams.getDatasource() + ": " + _errorMessage);
         } finally {
            try {
               if (rs != null) {
                  rs.close();
               }
            } catch (SQLException e) { /* do nothing */ }
         }
      }
      t.indices = result;
   }

   /**
    * Returns database meta data.
    *
    * Returned object must be closed using "closeDatabaseMetaData"
    *
    * @param jdbcName Database which is configured at "java:comp/env/jdbc/<jdbcName>"
    * @return Connection or null if none could be made. _errorMessage contains reason in the latter case.
    */
   private DatabaseMetaData openDatabaseMetaData(String jdbcName) {
      Context jndiContext = null;
      DatabaseMetaData dbmd = null;
      try {
         jndiContext = new InitialContext();
         javax.sql.DataSource ds = (javax.sql.DataSource) jndiContext.lookup("java:comp/env/jdbc/" + _macroParams.getDatasource());
         try {
            _con = ds.getConnection();
            dbmd = _con.getMetaData();
         } catch (SQLException ex) {
            _errorMessage = ex.getMessage();
            log.error("SQLException " + _macroParams.getDatasource() + ": " + _errorMessage);
         }
      } catch (NamingException ex) {
         _errorMessage = ex.getMessage();
         log.error("NamingException " + _macroParams.getDatasource() + ": " + _errorMessage);
      } finally {
         if (jndiContext != null) {
            try {
               jndiContext.close();
            } catch (NamingException ex2) { /* do nothing */ }
         }
      }
      return dbmd;
   }

   /**
    * Close connection which is used by database meta data object.
    *
    * @param dbmd Database meta data retrieved by calling openDatabaseMetaData
    */
   private void closeDatabaseMetaData(DatabaseMetaData dbmd) {
      try {
         if (_con != null) {
            _con.close();
         }
      } catch (SQLException ex) { /* do nothing */ }
   }
}
