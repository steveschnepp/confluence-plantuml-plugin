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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sourceforge.plantuml.core.DiagramType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * This is the abstract implementation class of the {database-structure} macro.
 */
abstract class AbstractDatabaseStructureMacroImpl {

   private static final Logger log = LoggerFactory.getLogger(AbstractDatabaseStructureMacroImpl.class);
   protected DatabaseStructureMacroParams _macroParams;
   protected String _errorMessage;
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
   public String createDotForDatabaseStructure(Map<String, String> params, PageContext pageContext,
         SpaceManager spaceManager,
         PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager,
         ContentPropertyManager contentPropertyManager) {

      _macroParams = new DatabaseStructureMacroParams(params);
      final DatabaseMetaData dbmd = openDatabaseMetaData();
      long[] times = new long[7];
      try {
         times[0] = System.currentTimeMillis();
         Map<String, TableDef> tables = getTables(dbmd);
         times[1] = System.currentTimeMillis();
         linkColumnsWithTables(tables, filterColumnsByName(getColumns(dbmd), _macroParams.getColumnNameRegEx()));
         times[2] = System.currentTimeMillis();
         tables =
               filterTablesByName(filterTablesByType(tables, _macroParams.getTableTypes()),
                     _macroParams.getTableNameRegEx());
         times[3] = System.currentTimeMillis();
         for (Map.Entry<String, TableDef> entry : tables.entrySet()) {
            addIndexDetails(dbmd, tables.get(entry.getKey()));
         }
         times[4] = System.currentTimeMillis();
         List<KeysDef> keys = reduceToTableReferences(getForeignKeys(dbmd, tables));
         times[5] = System.currentTimeMillis();
         String s = buildDot(tables, keys);
         times[6] = System.currentTimeMillis();

         if (log.isInfoEnabled()) {
            log.info("Preparting DOT diagramm took " + (times[6] - times[0]) + " ms ("
                  + "Tables " + (times[1] - times[0]) + ", Columns " + (times[2] - times[1])
                  + ", Filter Tables " + (times[3] - times[2]) + ", Indexes " + (times[4] - times[3])
                  + ", Foreign Keys " + (times[5] - times[4]) + ", DOT " + (times[6] - times[5]) + ")");
         }
         return s;
      } finally {
         closeDatabaseMetaData();
      }
   }

   private String buildDot(Map<String, TableDef> tables, List<KeysDef> keys) {
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

            final StringBuilder labelStringBuilder = new StringBuilder();
            buildTableName(labelStringBuilder, table);
            buildColumns(labelStringBuilder, table);
            buildIndexesInTables(labelStringBuilder, table);
            sb.append(GraphVizUtils.toNodeLabel(labelStringBuilder.toString()));

            sb.append("\"\nshape=\"record\"];\n");
            buildIndexRelations(sb, table, tables);
         }
         buildTableRelationsFromForeignKeys(sb, keys, tables);
         buildTableRelationsFromRegEx(sb, tables);
      }
      sb.append("}\n");

      log.info(sb.toString());
      return sb.toString();
   }

   /**
    * Replaces characters in names which are not allowed as NodeId.
    *
    * @param s String to be used as NodeId
    * @return Cleaned string
    */
   private String cleanNodeId(String s) {
      return s.replaceAll("\\.", "_")
            .replaceAll("\\$", "_S_")
            .replaceAll("<", "_")
            .replaceAll(">", "_");
   }

   private void buildTableName(StringBuilder sb, TableDef table) {
      if (_macroParams.getTableTypes().size() != 1) {
         sb.append("«").append(table.getTableType()).append("»\\n");
      }
      if (_macroParams.getSchemaName() == null) {
         sb.append(table.getTableSchema()).append(".");
      }
      sb.append(table.getTableName());
   }

   private void buildColumns(final StringBuilder sb, TableDef table) {
      if (_macroParams.isShowColumns() && !table.getColumns().isEmpty()) {
         sb.append(" | {");
         for (ColumnDef c : table.getColumns()) {
            sb.append("+ ").append(c.getColumnName()).append(": ").append(c.getTypeName());
            if (c.getTypeName().contains("char") || c.getTypeName().contains("CHAR")) {
               sb.append("(").append(c.getColumnSize()).append(")");
            }
            if (c.getTypeName().startsWith("num") || c.getTypeName().startsWith("NUM")) {
               sb.append("(").append(c.getColumnSize()).append(",").append(c.getDecimalDigits()).append(")");
            }
            if (c.getDefaultValue() != null) {
               sb.append(" =  ").append(c.getDefaultValue().replaceAll("::.*", "")); // 'value'::datatype
            }
            if (c.getNullable() == 0) {
               sb.append(" \\{not null\\}");
            }
            if (c.getComment() != null) {
               sb.append(" -- ").append(c.getComment());
            }
            sb.append("\\l");
         }
         sb.append("}");
      }
   }

   private void buildIndexesInTables(final StringBuilder sb, TableDef table) {
      if (_macroParams.isShowIndexes() && !table.getIndices().isEmpty()) {
         boolean first = true;
         sb.append(" | {");
         // data are ordered
         for (IndexDef ix : table.getIndices()) {
            if (ix.getOrdinalPosition() == 1) {
               if (!first) {
                  sb.append("\\l");
               } else {
                  first = false;
               }
               sb.append(ix.getIndexName()).append(":");
            }
            if (ix.getColumnName() != null) {
               sb.append(" ").append(ix.getColumnName().replaceAll("\"", ""));
            }
         }
         sb.append("\\l}");
      }
   }

   /**
    * Prints relation between two nodes.
    *
    * Output: node1 -> node2 [color="blue"];\n Node names will be cleaned from special characters.
    *
    * @param sb StringBuilder to write relation to.
    * @param node1 Left node
    * @param node2 Right node
    * @param format Additional formatting.
    */
   private void printRelation(StringBuilder sb, String node1, String node2, String format) {
      sb.append(cleanNodeId(node1))
            .append(" -> ")
            .append(cleanNodeId(node2))
            .append(" ").append(format).append(";\n");
   }

   /**
    * Creates link between Index and table where it is defined.
    *
    * Link will have no arrowhead and a dark grey color.
    *
    * @param sb StringBuilder to write relation to
    * @param currentTable Table for which references will be created
    * @param tables All available tables/indexes
    */
   private void buildIndexRelations(final StringBuilder sb, TableDef currentTable, Map<String, TableDef> tables) {
      for (IndexDef ix : currentTable.getIndices()) {
         if (ix.getOrdinalPosition() == 1) {
            for (TableDef referencedTable : tables.values()) {
               if (ix.getIndexName().equals(referencedTable.getTableName())) {
                  printRelation(sb, currentTable.getTableId(), referencedTable.getTableId(),
                        "[arrowhead=none, color=\"#999999\"]");
               }
            }
         }
      }
   }

   /**
    * Creates link between tables based on foreign keys.
    *
    * @param sb StringBuilder to write to
    * @param keys All available foreign keys
    * @param tables All available tables
    */
   private void buildTableRelationsFromForeignKeys(final StringBuilder sb, List<KeysDef> keys,
         Map<String, TableDef> tables) {
      for (KeysDef key : keys) {
         if (tables.containsKey(key.getFkTableId()) && tables.containsKey(key.getPkTableId())) {
            printRelation(sb, key.getPkTableId(), key.getFkTableId(), "");
         }
      }
   }

   /**
    * Create link between tables based on a regular expression given as macro parameter.
    *
    * @param sb StringBuilder to write to
    * @param tables All available tables
    */
   private void buildTableRelationsFromRegEx(final StringBuilder sb, Map<String, TableDef> tables) {
      if (_macroParams.getRelationRegEx() == null) {
         return;
      }

      final Pattern pattern = Pattern.compile(_macroParams.getRelationRegEx());

      for (Map.Entry<String, TableDef> tmp : tables.entrySet()) {
         final TableDef outerTable = tmp.getValue();
         for (ColumnDef outerColumn : outerTable.getColumns()) {
            final String outer = outerTable.getTableName() + "." + outerColumn.getColumnName() + " ";

            for (Map.Entry<String, TableDef> innerTmp : tables.entrySet()) {
               final TableDef innerTable = innerTmp.getValue();
               final List<ColumnDef> innerColumns = innerTable.getColumns();
               for (ColumnDef innerColumn : innerColumns) {
                  final String reference = outer + innerTable.getTableName() + "." + innerColumn.getColumnName();
                  if (pattern.matcher(reference).matches()) {
                     printRelation(sb, outerTable.getTableId(), innerTable.getTableId(), "");
                  }
               }
            }
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
      for (Map.Entry<String, TableDef> entry : tables.entrySet()) {
         final TableDef t = entry.getValue();
         if (t.getTableType() != null && tableTypes.contains(t.getTableType())) {
            result.put(entry.getKey(), t);
         }
      }
      return result;
   }

   private Map<String, TableDef> filterTablesByName(Map<String, TableDef> tables, String tableNameRegEx) {
      if (StringUtils.isEmpty(tableNameRegEx)) {
         return tables;
      }

      final Map<String, TableDef> result = new HashMap<String, TableDef>();
      for (Map.Entry<String, TableDef> entry : tables.entrySet()) {
         final TableDef t = entry.getValue();
         if (t.getTableName().matches(tableNameRegEx)) {
            result.put(entry.getKey(), t);
         }
      }
      return result;
   }

   private List<ColumnDef> filterColumnsByName(List<ColumnDef> columns, String columnNameRegEx) {
      if (StringUtils.isEmpty(columnNameRegEx)) {
         return columns;
      }

      final List<ColumnDef> result = new LinkedList<ColumnDef>();
      for (ColumnDef c : columns) {
         if (c.getColumnName().matches(columnNameRegEx)) {
            result.add(c);
         }
      }
      return result;
   }

   private void sqlException(String datasource, SQLException e) {
      _errorMessage = e.getMessage();
      log.error("SQLException " + datasource, e);
   }

   private void closeResource(ResultSet rs) {
      try {
         if (rs != null) {
            rs.close();
         }
      } catch (SQLException e) {
         log.debug("Exception closing ResultSet", e);
      }
   }

   private void linkColumnsWithTables(Map<String, TableDef> tables, List<ColumnDef> columns) {
      for (ColumnDef column : columns) {
         tables.get(column.getTableId()).getColumns().add(column);
      }
   }

   private Map<String, TableDef> getTables(DatabaseMetaData dbmd) {
      final Map<String, TableDef> result = new HashMap<String, TableDef>();

      if (_errorMessage == null) {
         ResultSet rs = null;
         try {
            rs = dbmd.getTables(null, _macroParams.getSchemaName(), _macroParams.getTableNameFilter(), null);
            while (rs.next()) {
               TableDef tmp = new TableDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
               result.put(tmp.getTableId(), tmp);
               if (log.isDebugEnabled()) {
                  log.debug(tmp.display());
               }
            }
         } catch (SQLException e) {
            sqlException(_macroParams.getDatasource(), e);
         } finally {
            closeResource(rs);
         }
      }
      return result;
   }

   private List<ColumnDef> getColumns(DatabaseMetaData dbmd) {
      final List<ColumnDef> result = new LinkedList<ColumnDef>();

      if (_errorMessage == null && _macroParams.isShowColumns()) {
         ResultSet rs = null;
         try {
            rs =
                  dbmd.getColumns(null, _macroParams.getSchemaName(), _macroParams.getTableNameFilter(),
                        _macroParams.getColumnNameFilter());
            while (rs.next()) {
               final String comment = _macroParams.isShowComments() ? rs.getString(12) : null;
               final String defaultValue = _macroParams.isShowDefaults() ? rs.getString(13) : null;

               final ColumnDef tmp = new ColumnDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                     rs.getString(6), rs.getInt(7), rs.getInt(9), rs.getInt(11), comment, defaultValue);
               result.add(tmp);
               if (log.isDebugEnabled()) {
                  log.debug(tmp.getColumnId());
               }
            }
         } catch (SQLException e) {
            sqlException(_macroParams.getDatasource(), e);
         } finally {
            closeResource(rs);
         }
      }
      return result;
   }

   private List<KeysDef> getForeignKeys(DatabaseMetaData dbmd, Map<String, TableDef> tables) {
      final List<KeysDef> result = new LinkedList<KeysDef>();

      if (_errorMessage == null && _macroParams.isUseForeingKeys()) {
         for (Map.Entry<String, TableDef> entries : tables.entrySet()) {
            final String tableName = entries.getValue().getTableName();

            ResultSet rs = null;
            try {
               rs = dbmd.getImportedKeys(null, _macroParams.getSchemaName(), tableName);
               while (rs.next()) {
                  KeysDef tmp =
                        new KeysDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                              rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
                  result.add(tmp);
                  if (log.isDebugEnabled()) {
                     log.debug(tmp.getKeysColumnId());
                  }
               }
            } catch (SQLException e) {
               sqlException(_macroParams.getDatasource(), e);
            } finally {
               closeResource(rs);
            }
         }
      }
      return result;
   }

   private void addIndexDetails(DatabaseMetaData dbmd, TableDef t) {
      final List<IndexDef> result = new LinkedList<IndexDef>();

      if (_errorMessage == null && (_macroParams.isShowIndexes() || _macroParams.getTableTypes().contains("INDEX"))) {
         ResultSet rs = null;
         try {
            rs = dbmd.getIndexInfo(t.getTableCatalog(), t.getTableSchema(), t.getTableName(), false, true);
            while (rs.next()) {
               IndexDef tmp = new IndexDef(rs.getString(1), rs.getString(2), rs.getString(3),
                     rs.getString(5), rs.getString(6), rs.getShort(8), rs.getString(9));
               result.add(tmp);
               if (log.isDebugEnabled()) {
                  log.debug(tmp.getIndexId());
               }
            }
         } catch (SQLException e) {
            sqlException(_macroParams.getDatasource(), e);
         } finally {
            closeResource(rs);
         }
      }
      t.setIndices(result);
   }

   /**
    * Returns database meta data.
    *
    * Returned object must be closed using "closeDatabaseMetaData"
    *
    * @param jdbcName Database which is configured at "java:comp/env/jdbc/<jdbcName>"
    * @return Connection or null if none could be made. _errorMessage contains reason in the latter case.
    */
   private DatabaseMetaData openDatabaseMetaData() {
      Context jndiContext = null;
      DatabaseMetaData dbmd = null;
      try {
         jndiContext = new InitialContext();
         javax.sql.DataSource ds =
               (javax.sql.DataSource) jndiContext.lookup("java:comp/env/jdbc/" + _macroParams.getDatasource());
         try {
            _con = ds.getConnection();
            dbmd = _con.getMetaData();
         } catch (SQLException e) {
            sqlException(_macroParams.getDatasource(), e);
         }
      } catch (NamingException e) {
         _errorMessage = e.getMessage();
         log.error("NamingException " + _macroParams.getDatasource() + _errorMessage, e);
      } finally {
         if (jndiContext != null) {
            try {
               jndiContext.close();
            } catch (NamingException e2) {
               log.debug("Exception closing JNDI context", e2);
            }
         }
      }
      return dbmd;
   }

   /**
    * Close connection which is used by database meta data object.
    *
    * @param dbmd Database meta data retrieved by calling openDatabaseMetaData
    */
   private void closeDatabaseMetaData() {
      try {
         if (_con != null) {
            _con.close();
         }
      } catch (SQLException e) {
         log.debug("Exception closing connection", e);
      }
   }
}
