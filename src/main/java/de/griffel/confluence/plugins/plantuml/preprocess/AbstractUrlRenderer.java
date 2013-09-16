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

import com.atlassian.confluence.spaces.Space;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Abstract UrlRenderer.
 */
public abstract class AbstractUrlRenderer implements UrlRenderer {

   /*
    * (non-Javadoc)
    * 
    * @see
    * de.griffel.confluence.plugins.plantuml.preprocess.UrlRenderer#buildDefaultAlias(de.griffel.confluence.plugins.
    * plantuml.preprocess.PreprocessingContext, de.griffel.confluence.plugins.plantuml.type.ConfluenceLink)
    */
   public String getDefaultAlias(PreprocessingContext context, ConfluenceLink link) {
      final StringBuilder sb = new StringBuilder();
      final Space space = context.getSpaceManager().getSpace(link.getSpaceKey());
      sb.append(space.getName());
      sb.append(" - ");
      sb.append(link.getPageTitle());
      if (link.hasFragment()) {
         sb.append("#");
         sb.append(link.getFragment());
      }
      return sb.toString();
   }

}
