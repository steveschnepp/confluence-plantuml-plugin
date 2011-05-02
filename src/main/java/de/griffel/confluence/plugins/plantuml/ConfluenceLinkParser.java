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

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.renderer.PageContext;

/**
 * ConfluenceLinkParser can be used to parse a string to a {@link ConfluenceLink}. The following string representations
 * are supporte:
 * 
 * <pre>
 * ^attachment.ext
 * or
 * pagetitle
 * pagetitle^attachment.ext
 * or
 * spacekey:pagetitle
 * spacekey:pagetitle^attachment.ext
 * </pre>
 */
public final class ConfluenceLinkParser {
   private final PageContext _pageContext;

   public ConfluenceLinkParser(PageContext context) {
      _pageContext = context;
   }

   public ConfluenceLink parse(String link) {
      final String spaceKey;
      final String pageTitle;
      final String attachmentName;
      if (link.contains("^")) {
         final String[] parts = link.split("[\\^:]");
         if (parts.length > 1 && !StringUtils.isEmpty(parts[0])) {
            if (parts.length > 2) {
               spaceKey = parts[0];
               pageTitle = parts[1];
               attachmentName = parts[2];
            } else {
               spaceKey = _pageContext.getSpaceKey();
               pageTitle = parts[0];
               attachmentName = parts[1];
            }
         } else {
            spaceKey = _pageContext.getSpaceKey();
            pageTitle = _pageContext.getPageTitle();
            attachmentName = parts[1];
         }
      } else {
         attachmentName = null;
         final String[] parts = link.split(":");
         if (parts.length > 1) {
            spaceKey = parts[0];
            pageTitle = parts[1];
         } else {
            spaceKey = _pageContext.getSpaceKey();
            pageTitle = link;
         }
      }
      return new ConfluenceLink(spaceKey, pageTitle, attachmentName);
   }
}
