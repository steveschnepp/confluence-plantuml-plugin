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

import com.atlassian.confluence.languages.LocaleManager;
import java.util.Map;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.HashMap;
import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is the abstract implementation class of the {DatabaseInfo} macro.
 */
abstract class AbstractDatabaseInfoMacroImpl {
    private final I18NBeanFactory _i18NBeanFactory;
    private final LocaleManager   _localeManager;

    private static final String KEY_ATTRIBUTE      = "plantuml.database-info.attribute";
    private static final String KEY_ERROR          = "plantuml.database-info.error.error";
    private static final String KEY_DS_NOT_EXIST   = "plantuml.database-info.error.datasource_not_exist";
    
    public AbstractDatabaseInfoMacroImpl(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        _i18NBeanFactory = i18NBeanFactory;
        _localeManager = localeManager;
    }
    
    public String execute(Map<String, String> params, RenderContext context) throws MacroException {
        return executeMyMacro(params, context);
    }

    protected abstract String executeMyMacro(Map<String, String> params, RenderContext context)
            throws MacroException;

    /**
     * Create HTML table
     */
    public String createResult(Map<String, String> params, PageContext pageContext) {
        final DatabaseInfoMacroParams macroParams = new DatabaseInfoMacroParams(params);
        final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
        final List<String> datasources = macroParams.getDatasources();

        for (String datasourceName : datasources) { 
            final DataSource ds = DatasourceHelper.getDatasource(datasourceName);
            final Map m;
            if (ds == null) {
                m = new HashMap<String,String>();
                m.put(getLocalizedMessage(KEY_ERROR), getLocalizedMessage(KEY_DS_NOT_EXIST));
            } else {
                m = DatasourceHelper.getDatabaseMetadata(ds, macroParams.getAttributes());
            }
            data.put(datasourceName, m);
        }
        return formatTable(data, datasources);
    }

    /**
     * Merges keys of multiple maps in one set
     *
     * @param map Map whose keys should be collected in *keys
     *
     * @param keys
     */
    public void collectAllKeys(Map<String, String> map, Set keys) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            keys.add(entry.getKey());
        }
    }

    /**
     * Create HTML output
     *
     * @param data Connection parameters of all data sources
     * @param datasourceNames List of data sources to show - defines order in which they are shown
     * @return
     */
    public String formatTable(Map<String, Map<String, String>> data, List<String> datasourceNames) {
        final Set<String> allAttributes = new TreeSet<String>();
        final StringBuilder sb = new StringBuilder("<table><tr><th>");
        sb.append(getLocalizedMessage(KEY_ATTRIBUTE)).append("</th>");

        for (String datasourceName : datasourceNames) {
            sb.append("<th>").append(datasourceName).append("</th>");
            collectAllKeys(data.get(datasourceName), allAttributes);
        }
        sb.append("</tr>\n");

        for (String key : allAttributes) {
            sb.append("<tr><td>").append(key).append("</td>");
            for (String datasourceName : datasourceNames) {
                String value = data.get(datasourceName).get(key);
                if (value != null && value.length() > 20) {
                    value = value.replaceAll(",", ", ");
                }
                sb.append("<td>").append(value).append("</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }
    
    private String getLocalizedMessage(String msgKey) {
        Locale locale = _localeManager.getLocale(AuthenticatedUserThreadLocal.getUser());
        return _i18NBeanFactory.getI18NBean(locale).getText(msgKey);
    }
}
