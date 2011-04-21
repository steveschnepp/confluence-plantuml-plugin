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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public enum DiagramType {
   UML("uml", "@startuml", "@enduml"),
   Ditaa("ditaa", "@startditaa", "@endditaa");

   private final String _type;
   private final String _startTag;
   private final String _endTag;

   private DiagramType(String type, String startTag, String endTag) {
      _type = type;
      _startTag = startTag;
      _endTag = endTag;
   }

   public String getType() {
      return _type;
   }

   public String getStartTag() {
      return _startTag;
   }

   public String getEndTag() {
      return _endTag;
   }

   public static DiagramType getDefault() {
      return UML;
   }

   public static DiagramType fromType(final String type) {
      return Iterators.find(Iterators.forArray(values()), new Predicate<DiagramType>() {
         public boolean apply(DiagramType diagramType) {
            return diagramType.getType().equals(type);
         }
      });
   }
}
