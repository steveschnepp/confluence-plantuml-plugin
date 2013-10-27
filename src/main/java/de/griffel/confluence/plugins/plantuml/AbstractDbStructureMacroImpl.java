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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This is the abstract implementation class of the {dbstructure} macro.
 */
abstract class AbstractDbStructureMacroImpl {
    protected ContentPropertyManager _cpm;
    protected PermissionManager _pm;
    protected String _baseUrl;
    protected DbStructureMacroParams _macroParams;
            

    public String execute(Map<String, String> params, String dotString, RenderContext context) throws MacroException {
        final SpaceGraphMacroParams macroParams = new SpaceGraphMacroParams(params);

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
        _pm = permissionManager;
        _cpm = contentPropertyManager;
        _macroParams = new DbStructureMacroParams(params);
        
        ClassLoader x = Thread.currentThread().getContextClassLoader();
        
        final Map<String,String> m = new HashMap<String,String>();
        try {
            Context jndiContext = new InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) jndiContext.lookup("java:comp/env/jdbc/" + _macroParams.getJdbcName());
            Connection con = ds.getConnection();
            DatabaseMetaData dbmd = con.getMetaData();
            ResultSet  t = dbmd.getTables(null, null, null, null);
            
            while (t.next()) {
                  m.put(t.getString(3), t.getString(4));
            }   
            t.close();
            con.close();
        } catch (NamingException ex) {
            Logger.getLogger(AbstractDbStructureMacroImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AbstractDbStructureMacroImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringBuilder sb = new StringBuilder("digraph g {\n");
        sb.append("edge [arrowsize=\"0.8\"];");
        sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"lightyellow\", fontname=\"Verdana\", fontsize=\"");
        sb.append(_macroParams.getNodeFontsize()).append("\"];\n");
        sb.append("rankdir=TB");
        for (String name : m.keySet()) {
            sb.append(name + " <- " + m.get(name) + "\n" );
        }
        sb.append("}\n");

        return sb.toString();
    }

}
