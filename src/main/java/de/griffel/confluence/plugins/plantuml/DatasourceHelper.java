/*
 * Copyright (C) 2011 Michael Griffel
 *
 * This program is free software: you can redistrib
 * it under the terms of the GNU General Public Lic
 * the Free Software Foundation, either version 3 o
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it 
 * but WITHOUT ANY WARRANTY; without even the impli
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURP
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Gener
 * along with this program.  If not, see <http://ww
 *
 * This distribution includes other third-party lib
 * These libraries and their corresponding licenses
 * from the GNU General Public License) are enumera
 *
 * PlantUML is a Open-Source tool in Java to draw U
 * The software is developed by Arnaud Roques at
 * http://plantuml.sourceforge.org.
 */
package de.griffel.confluence.plugins.plantuml;

import java.util.LinkedList;
import java.util.List;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatasourceHelper {
    public static String JDBC_CONTEXT = "java:comp/env/jdbc";
    
    /**
     * Determines data sources configured in Tomcat.
     * @return List of data source names
     */
    public static List<String> listAvailableDataSources() {
        final List<String> results = new LinkedList<String>();
        try {
            final InitialContext jndiContext = new InitialContext();
            final NamingEnumeration<Binding> bindings = jndiContext.listBindings(JDBC_CONTEXT);
            while (bindings != null && bindings.hasMore()) {
                results.add(bindings.next().getName());
            }
        } catch (NamingException e) {
            // do nothing
        }
        return results;
    }
    
    /**
     * Returns DataSource for given data source name.
     * @param name Name of data source as returned by listAvailableDataSources
     * @return DataSource if name exists, otherwise null
     */
    public static DataSource getDatasource(String name) {
        try {
            final InitialContext jndiContext = new InitialContext();
            return (javax.sql.DataSource) jndiContext.lookup(JDBC_CONTEXT + "/" + name);
        } catch (NamingException e) {
            return null;
        }
    }

}
