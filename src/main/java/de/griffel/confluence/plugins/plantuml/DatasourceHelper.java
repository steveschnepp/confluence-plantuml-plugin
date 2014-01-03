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

import com.opensymphony.xwork.interceptor.component.ComponentManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasourceHelper {

   public static final String JDBC_CONTEXT = "java:comp/env/jdbc";
   public static final String CATALOGS = "Catalogs";
   public static final String TABLE_TYPES = "TableTypes";
   public static final String ERROR = "<b>ERROR</b>";
   private static final Logger log = LoggerFactory.getLogger(DatasourceHelper.class);

   private DatasourceHelper() {
   }

   /**
    * Determines data sources configured in Tomcat.
    *
    * @return List of data source names
    */
   public static List<String> listAvailableDataSources() {
      final List<String> results = new LinkedList<String>();

      // Workaround for classloader problems, see https://answers.atlassian.com/questions/6374/how-do-i-access-jndi-from-a-version-2-osgi-plugin
      final ClassLoader origCL = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(ComponentManager.class.getClassLoader());
         final InitialContext jndiContext = new InitialContext();
         final NamingEnumeration<Binding> bindings = jndiContext.listBindings(JDBC_CONTEXT);
         while (bindings != null && bindings.hasMore()) {
            results.add(bindings.next().getName());
         }
      } catch (NamingException e) {
         log.debug("NamingException listBindings", e);
         results.add(e.toString());
      } finally {
         Thread.currentThread().setContextClassLoader(origCL);
      }
      return results;
   }

   /**
    * Returns DataSource for given data source name.
    *
    * @param name Name of data source as returned by listAvailableDataSources
    * @return DataSource if name exists, otherwise null
    */
   public static DataSource getDatasource(String name) {
      final ClassLoader origCL = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(ComponentManager.class.getClassLoader());
         final InitialContext jndiContext = new InitialContext();
         return (javax.sql.DataSource) jndiContext.lookup(JDBC_CONTEXT + "/" + name);
      } catch (NamingException e) {
         log.debug("NamingException lookup", e);
         return null;
      } finally {
         Thread.currentThread().setContextClassLoader(origCL);
      }
   }

   /**
    * Returns Database meta data.
    *
    * @param datasource data source
    * @param attributes attributes to get
    * @return Map with requested attributes
    */
   public static Map<String, String> getDatabaseMetadata(DataSource datasource, List<String> attributes) {
      final Map<String, String> databaseMetadata = new HashMap<String, String>();
      Connection con = null;
      try {
         con = datasource.getConnection();
         final DatabaseMetaData dbmd = con.getMetaData();
         for (String attribute : attributes) {
            if (CATALOGS.equals(attribute)) {
               databaseMetadata.put(CATALOGS, StringUtils.join(getCatalogs(dbmd), ","));
            } else if (TABLE_TYPES.equals(attribute)) {
               databaseMetadata.put(TABLE_TYPES, StringUtils.join(getTableTypes(dbmd), ","));
            } else {
               fillValue(dbmd, attribute, databaseMetadata);
            }
         }
      } catch (SQLException e) {
         log.debug("SQLException accessing metadata", e);
         databaseMetadata.put(ERROR, e.getMessage());
      } finally {
         try {
            if (con != null) {
               con.close();
            }
         } catch (SQLException ex) {
            log.debug("SQLException closing connection", ex);
         }
      }
      return databaseMetadata;
   }

   private static List<String> getCatalogs(DatabaseMetaData dbmd) throws SQLException {
      final List<String> catalogNames = new LinkedList<String>();
      final ResultSet rs = dbmd.getCatalogs();
      while (rs.next()) {
         catalogNames.add(rs.getString(1));
      }
      rs.close();
      return catalogNames;
   }

   public static List<String> getTableTypes(DatabaseMetaData dbmd) throws SQLException {
      final List<String> tableTypes = new LinkedList<String>();
      final ResultSet rs = dbmd.getTableTypes();
      while (rs.next()) {
         tableTypes.add(rs.getString(1));
      }
      rs.close();
      return tableTypes;
   }

   /**
    * Gets attribute from Database meta data and stores it in map.
    *
    * @param dbmd Database meta data
    * @param attribute Name of attribute to get
    * @param result Map to store attribute name and value in
    */
   private static void fillValue(DatabaseMetaData dbmd, String attribute, Map<String, String> result) {
      final Class[] noparams = {};
      try {
         final Method m = dbmd.getClass().getMethod("get" + attribute, noparams);
         result.put(attribute, "" + m.invoke(dbmd, null));
      } catch (IllegalAccessException e) {
         log.debug("IllegalAccess", e);
         result.put(attribute, ERROR);
      } catch (IllegalArgumentException e) {
         log.debug("IllegalArgument", e);
         result.put(attribute, ERROR);
      } catch (NoSuchMethodException e) {
         log.debug("NoSuchMethod", e);
         result.put(attribute, ERROR);
      } catch (SecurityException e) {
         log.debug("Security", e);
         result.put(attribute, ERROR);
      } catch (InvocationTargetException e) {
         log.debug("InvocationTarget", e);
         result.put(attribute, ERROR);
      }
   }
}
