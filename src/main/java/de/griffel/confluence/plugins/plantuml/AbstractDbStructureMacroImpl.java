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

import java.util.Map;

import net.sourceforge.plantuml.core.DiagramType;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;

/**
 * This is the abstract implementation class of the {dbstructure} macro.
 */
abstract class AbstractDbStructureMacroImpl {
    protected ContentPropertyManager _cpm;
    protected PermissionManager _pm;
    protected String _baseUrl;
    protected DbStructureMacroParams _macroParams;
    protected String _errorMessage;
            

    public String execute(Map<String, String> params, String dotString, RenderContext context) throws MacroException {
        final DbStructureMacroParams macroParams = new DbStructureMacroParams(params);

        params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
        if (macroParams.isDebug()) {
            params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.TRUE.toString());
        }

        return executePlantUmlMacro(params, dotString, context);
    }

    protected abstract String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
            throws MacroException;

  
    /**
     * Create dot-String {dbstructure}
     */
    public String createDotForDbStructure(Map<String, String> params, PageContext pageContext, SpaceManager spaceManager,
            PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager,
            ContentPropertyManager contentPropertyManager) {
        
        _baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        _macroParams = new DbStructureMacroParams(params);        
        
        final DatabaseMetaData dbmd  = getDatabaseConnection(_macroParams.getDatasource());
        Map<String, TableDef> tables = getTables(dbmd);
        List<ColumnDef> columns      = getColumns(dbmd);
        List<KeysDef> keys           = getForeignKeys(dbmd);
        if (dbmd == null) {
            _errorMessage = "Datasource " + _macroParams.getDatasource() + " does not exist.";
        } else {
            try { dbmd.getConnection().close(); } catch (SQLException ex) { /* do nothing */ }
                
            columns = filterColumnsByName(columns, _macroParams.getColumnNameRegEx());
            linkColumnsWithTables(tables, columns);
            columns = null;
            tables = filterTablesByName(filterTablesByType(tables, "TABLE"), _macroParams.getTableNameRegEx());
        }
        final StringBuilder sb = new StringBuilder("digraph g {\n");
        sb.append("edge [arrowsize=\"0.8\"];");
        sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"lightyellow\", fontname=\"Verdana\", fontsize=\"");
        sb.append(_macroParams.getNodeFontsize()).append("\"];\n");
        sb.append("rankdir=LR\n");
        
        if (_errorMessage != null){
            sb.append("\"").append(_errorMessage).append("\"");
        } else { 
            sb.append(createDbDot(tables, columns, keys));
        }
        sb.append("}\n");

        return sb.toString();
    }
    
    private String createDbDot(Map<String, TableDef> tables, List<ColumnDef> columns, List<KeysDef> keys) {
        final StringBuilder sb = new StringBuilder();

        for (TableDef table : tables.values()) {
            // "TableName" [ label = "TableName | {{ColumnName1|ColumnName2} | {ColumnType1|ColumnType2}}" 
            // shape="record"];
            sb.append(table.tableName).append(" [ label = \"").append(table.tableName);

            if (table.columns.size() > 0) {
                String[] names = new String[table.columns.size()];
                String[] types = new String[table.columns.size()];
                int i = 0;
                for (ColumnDef c : table.columns) {
                    names[i] = "<" + c.columnName + "_to> " + c.columnName; // fieldId to link
                    types[i] = "<" + c.columnName + "_from> " + c.typeName;
                    if (c.typeName.startsWith("var")) {
                        types[i] = types[i] + "(" + c.columnSize + ")";
                    }
                    if (c.nullable == 0) {
                        types[i] = types[i] + " (not null)";
                    }
                    i++;
                }
                sb.append(" | {{")
                        .append(StringUtils.join(names, "|"))
                        .append("} | {")
                        .append(StringUtils.join(types, "|"))
                        .append("}}");
            }
            sb.append("\"\n")
                    .append("shape=\"record\"];\n");
        }

        for (KeysDef key : keys) {
            if (tables.containsKey(key.getFkId()) && tables.containsKey(key.getPkId())) {
                sb.append(key.tableNamePk).append(":").append(key.columnNamePk).append("_from")
                  .append(" -> ")
                  .append(key.tableNameFk).append(":").append(key.columnNameFk);
                
                if (key.tableNamePk.equals(key.tableNameFk)) {
                    sb.append("_from;\n");
                } else {
                    sb.append("_to;\n");
                }
            }
        }
        return sb.toString();
    }

    private class BaseDef {
        String tableCatalog; // may be null
        String tableSchema;  // may be null
        String tableName;
        
        String getId() {            
            return tableCatalog + "#" + tableSchema + "#" + tableName;
        }
    }
    
    private class ColumnDef extends BaseDef {
        String columnName;
        int    dataType;        // SQL type from java.sql.Types        
        String typeName;        // Data source dependent type name, for a UDT the type name is fully qualified
        int    columnSize;
        // int bufferLength;    // not used
        int decimalDigits;      // the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
        int numPrecRadix;       //  Radix (typically either 10 or 2)
        int nullable;           // is NULL allowed. columnNoNulls - might not allow NULL values,  columnNullable - definitely allows NULL values, columnNullableUnknown - nullability unknown
        String remarks;         // comment describing column (may be null)
        String columnDefaultValue; // default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
        // int sqlDataType;     // unused
        // int sqlDateTimeSub;  // unused
        int charOctetLenght;    // for char types the maximum number of bytes in the column        
        int ordinalPosition;    // index of column in table (starting at 1)        
        // String isNullable;   // ISO rules are used to determine the nullability for a column.  YES, NO, empty string (if the nullability is unknown)
        // String scopeCatalog; // catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
        // String scopeSchema;  // schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
        // String scopeTable;   // table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
        // short sourceDataType;     //  source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
        // String isAutoincrement;   // Indicates whether this column is auto incremented: YES, NO, empty string (if it cannot be determined)
        // String isGeneratedColumn; // Indicates whether this is a generated column: YES, NO, empty string (if it cannot be determined)
        
        ColumnDef(String tc, String ts, String tn, String cn, int dt,
                String tyn, int cs, int dd, int radix, int n,
                String r, String defVal, int length, int op) {
            tableCatalog = tc;
            tableSchema = ts;
            tableName = tn;
            columnName = cn;
            dataType = dt;
            typeName = tyn;
            columnSize = cs;
            decimalDigits = dd;
            numPrecRadix = radix;
            nullable = n;
            remarks = r;
            columnDefaultValue = defVal;
            charOctetLenght = length;
            ordinalPosition = op;
        }
    }

    private class TableDef extends BaseDef {
        String tableType;    // Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
        String remarks;      // explanatory comment on the table, may be null
        // String typeCatalog;  // may be null
        // String typeSchema;   // may be null
        // String typeName;     // may be null
        // String selfReferencingColName; // name of the designated "identifier" column of a typed table, may be null
        // String refGeneration; // specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) 
        List<ColumnDef> columns;
        
        TableDef(String tc, String ts, String tn, String tt, String r) {
            tableCatalog = tc;
            tableSchema = ts;
            tableName = tn;
            tableType = tt;
            remarks = r;
            columns = new LinkedList<ColumnDef>();
        }
    }
    
    private class KeysDef {
        String tableCatalogPk; // may be null
        String tableSchemaPk;  // may be null
        String tableNamePk;
        String columnNamePk;
        String tableCatalogFk; // may be null
        String tableSchemaFk;  // may be null
        String tableNameFk;
        String columnNameFk;
        short  keySeqNumber; // sequence number within a foreign key
        // short updateRule;
        // short deleteRule;
        String namePk; // may be null
        String nameFk; // may be null
        // short deferability;
        
        KeysDef(String tcp, String tsp, String tnp, String cnp,
                String tcf, String tsf, String tnf, String cnf,
                short sn, String np, String nf) {
            tableCatalogPk = tcp;
            tableSchemaPk = tsp;
            tableNamePk = tnp;
            columnNamePk = cnp;            
            tableCatalogFk = tcf;
            tableSchemaFk = tsf;
            tableNameFk = tnf;
            columnNameFk = cnf;            
            keySeqNumber = sn;
            namePk = np;
            nameFk = nf;
        }
        String getPkId() {
            return tableCatalogPk + "#" + tableSchemaPk + "#" + tableNamePk;
        }
        String getFkId() {
            return tableCatalogFk + "#" + tableSchemaFk + "#" + tableNameFk;
        }
    }

    private Map<String, TableDef> filterTablesByType(Map<String, TableDef> tables, String tableTypeCriteria) {
        if (tableTypeCriteria == null || tableTypeCriteria.isEmpty()) {
            return tables;
        }

        final Map<String, TableDef> result = new HashMap<String, TableDef>();
        for (String key : tables.keySet()) {
            final TableDef t = tables.get(key);
            if (t.tableType != null && t.tableType.matches(tableTypeCriteria)) {
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
            tables.get(column.getId()).columns.add(column);
        }
    }
    
    private List<ColumnDef> getColumns(DatabaseMetaData dbmd) {
        List<ColumnDef> result = new LinkedList<ColumnDef>();
        
        if (dbmd == null) {
            return result;
        }
        ResultSet rs = null;
        try {
            rs = dbmd.getColumns(null, _macroParams.getSchemaNameFilter(), _macroParams.getTableNameFilter(), _macroParams.getColumnNameFilter());
            while (rs.next()) {
                result.add(new ColumnDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
                                rs.getString(6), rs.getInt(7),rs.getInt(9), rs.getInt(10),  rs.getInt(11), 
                        rs.getString(12), rs.getString(13), rs.getInt(16), rs.getInt(17)));
            }   
         } catch (SQLException e) {
              _errorMessage = e.getMessage();
         } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    // do nothing
                }
         }
        return result;
    }
    
    private Map<String, TableDef> getTables(DatabaseMetaData dbmd) {
        Map<String, TableDef> result = new HashMap<String, TableDef>();
        
        if (dbmd == null) {
            return result;
        }
        ResultSet rs = null;
        try {
            rs = dbmd.getTables(null, _macroParams.getSchemaNameFilter(), _macroParams.getTableNameFilter(), null);
            while (rs.next()) {
                TableDef tmp = new TableDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                result.put(tmp.getId(), tmp);
            }
         } catch (SQLException e) {
              _errorMessage = e.getMessage();
         } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    // do nothing
                }
         }
        return result;
    }

    private List<KeysDef> getForeignKeys(DatabaseMetaData dbmd) {
        List<KeysDef> result = new LinkedList<KeysDef>();
        
        if (dbmd == null) {
            return result;
        }
        ResultSet rs = null;
        try {
            rs = dbmd.getImportedKeys(null, _macroParams.getSchemaNameFilter(), _macroParams.getTableNameFilter());
            while (rs.next()) {
                KeysDef tmp = new KeysDef(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getShort(9), rs.getString(12), rs.getString(13));
                result.add(tmp);
            }   
         } catch (SQLException e) {
              _errorMessage = e.getMessage();
         } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    // do nothing
                }
         }
        return result;
    }

    /**
     * Returns database metadata
     * @param jdbcName  Database which is configured at "java:comp/env/jdbc/<jdbcName>"
     * @return Connection or null if none could be made. _errorMessage contains reason in the latter case.
     */
    private DatabaseMetaData getDatabaseConnection(String jdbcName) {
        Context jndiContext = null;
        DatabaseMetaData dbmd = null;
        try {
            jndiContext = new InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) jndiContext.lookup("java:comp/env/jdbc/" + _macroParams.getDatasource());
            try {
                Connection con = ds.getConnection();
                dbmd = con.getMetaData();
            } catch (SQLException ex) {
                _errorMessage = ex.getMessage();
            }
        } catch (NamingException ex) {
            _errorMessage = ex.getMessage();
        } finally {
            if (jndiContext != null) {
                try {
                    jndiContext.close();
                } catch (NamingException ex2 ) {
                    // do nothing
                }
            }
        }
        return dbmd;
    }
}
