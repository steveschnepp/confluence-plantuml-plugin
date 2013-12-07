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
import java.util.Map;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/datasource")
public class DatasourceRestResource {

    class GatewayTimeout implements Response.StatusType {
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

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage() {
        return Response.ok(new DatasourceListRestResourceModel(DatasourceHelper.listAvailableDataSources())).build();
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

}
