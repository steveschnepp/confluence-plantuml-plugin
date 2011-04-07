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

import java.util.Collections;
import java.util.Map;

/**
 * Supported PlantUML Macro parameters.
 */
public class PlantUmlMacroParams {

   public enum Param {
      title
   }

   @SuppressWarnings("rawtypes")
   private final Map _params;

   @SuppressWarnings("rawtypes")
   public PlantUmlMacroParams(Map params) {
      _params = params != null ? params : Collections.EMPTY_MAP;
   }

   public String getTitle() {
      return (String) _params.get(Param.title);
   }

   /*
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "PlantUmlMacroParams [_params=" + _params + "]";
   }

}
