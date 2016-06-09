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

import org.apache.commons.lang.StringUtils;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Page Anchor Builder.
 * <p/>
 * Default implementation for Confluence version <= 3.
 */
public class PageAnchorBuilder {
   String generateAnchor(ConfluenceLink link) {
      if (!link.hasFragment()) {
         throw new IllegalArgumentException("Confluence w/o section: " + link);
      }

      final StringBuilder sb = new StringBuilder();
      sb.append(link.getPageTitle());
      sb.append("-");
      sb.append(encodeFragment(link.getFragment()));

      final String result = "#" + UrlCoder.encode(StringUtils.deleteWhitespace(sb.toString()));
      return result;
   }

   protected String encodeFragment(String s) {
      String result = s;
      // Copied from com.atlassian.renderer.util.RendererUtil#stripBasicMarkup(String s)
      result = result.replaceAll("h[0-9]\\.", " "); // headings
      result = result.replaceAll("\\[.*///.*\\]", ""); // system links
      result = result.replaceAll("[\\[\\]\\*_\\^\\-\\~\\+]", ""); // basic formatting
      result = result.replaceAll("\\|", " "); // table breaks
      result = result.replaceAll("\\{([^:\\}\\{]+)(?::([^\\}\\{]*))?\\}(?!\\})", " "); // macros
      result = result.replaceAll("\\n", " ");
      result = result.replaceAll("\\r", " ");
      result = result.replaceAll("bq\\.", " ");
      result = result.replaceAll("  ", " ");

      return result;
   }
}
