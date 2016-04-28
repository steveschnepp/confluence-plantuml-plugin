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

import org.apache.commons.lang.StringEscapeUtils;

/**
 * GraphViz Utils.
 */
public final class GraphVizUtils {
   private GraphVizUtils() {
      // utility class - no instances allowed
   }

   /**
    * Converts the given string to a dot label string.
    *
    * @param s the string that should be used a label string.
    * @return dot label string.
    * @see http://www.graphviz.org/doc/info/attrs.html#k:lblString
    */
   public static String toNodeLabel(String s) {
      // HTML-escape the string twice since it will be unwrapped once in PlantUmlMacro#execute(...)
      return StringEscapeUtils.escapeHtml(
            StringEscapeUtils.escapeHtml(s));
   }
}
