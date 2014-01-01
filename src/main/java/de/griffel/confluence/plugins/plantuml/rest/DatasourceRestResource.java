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

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import de.griffel.confluence.plugins.plantuml.DatabaseInfoMacroParams;
import de.griffel.confluence.plugins.plantuml.DatasourceHelper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/datasource")
public class DatasourceRestResource {

   @GET
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response getMessage() {
      return Response.ok(new ListRestResourceModel(DatasourceHelper.listAvailableDataSources())).build();
   }

   @GET
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Path("/{name}")
   public Response getMessageFromPath(@PathParam("name") String datasourceName) {
      final DataSource ds = DatasourceHelper.getDatasource(datasourceName);
      if (ds == null) {
         return Response.status(Response.Status.NOT_FOUND).build();
      } else {
         final Map<String, String> m = DatasourceHelper.getDatabaseMetadata(ds, new DatabaseInfoMacroParams(null).getAllAvailableAttributes());
         if (m.size() < 2) {
            // m contains error message "cannot create connection to database"
            return Response.status(new GatewayTimeout()).build();
         } else {
            return Response.ok(new DatasourceRestResourceModel(m)).build();
         }
      }
   }

   @GET
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Path("/{name}/{detail}")
   public Response getMessageFromPath(@PathParam("name") String datasourceName, @PathParam("detail") String detail) {
      final DataSource ds = DatasourceHelper.getDatasource(datasourceName);
      if (ds == null) {
         return Response.status(Response.Status.NOT_FOUND).build();
      } else {
         final List<String> attributeName = new LinkedList<String>();
         attributeName.add(detail);
         final Map<String, String> attributes = DatasourceHelper.getDatabaseMetadata(ds, attributeName);
         final String result = attributes.get(detail);
         if (DatasourceHelper.ERROR.equals(result)) {
            return Response.status(Response.Status.NOT_FOUND).build();
         } else {
            return Response.ok(new ListRestResourceModel(dbMetadataAsList(detail, result))).build();
         }
      }
   }

   private List<String> dbMetadataAsList(String attributeName, String value) {
      // split comma separated items only if keyword ends in s
      if (attributeName.endsWith("s")) {
         return Arrays.asList(value.split(" *, *"));
      } else {
         final List<String> l = new LinkedList<String>();
         l.add(value);
         return l;
      }
   }

   static class GatewayTimeout implements Response.StatusType {

      @Override
      public Response.Status.Family getFamily() {
         return Response.Status.Family.SERVER_ERROR;
      }

      @Override
      public String getReasonPhrase() {
         return "Gateway Timeout";
      }

      @Override
      public int getStatusCode() {
         return 504;
      }
   }

}
