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
package de.griffel.confluence.plugins.plantuml.preprocess;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

public class ExternalUrlRenderer implements UrlRenderer {

   private final String _baseUrl;

   public ExternalUrlRenderer(String baseUrl) {
      _baseUrl = baseUrl;
   }

   /**
    * Returns the external display URL of this Confluence link.
    * 
    * @param link the Confluence link for which the external display URL should be generated.
    * @return the external display URL of this Confluence link.
    */
   public String render(ConfluenceLink link) {
      final StringBuilder sb = new StringBuilder();
      sb.append(_baseUrl);
      sb.append("/display/");
      sb.append(link.getSpaceKey());
      sb.append("/");
      sb.append(link.getPageTitle());
      if (link.hasSection()) {
         sb.append(link.toFragmentUrl());
      }
      return sb.toString();
   }
}