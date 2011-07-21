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
package de.griffel.confluence.plugins.plantuml.type;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import net.sourceforge.plantuml.DiagramType;
import net.sourceforge.plantuml.UmlSource;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

/**
 * Builder for {@link UmlSource}.
 */
public final class UmlSourceBuilder {
   private final List<String> _lines = Lists.newArrayList();
   private final DiagramType _diagramType;

   public UmlSourceBuilder() {
      this(null);
   }

   public UmlSourceBuilder(DiagramType diagramType) {
      _diagramType = diagramType;
      if (diagramType != null) {
         append(getStartTag());
      }
   }

   public UmlSourceBuilder append(String lineOrMuliLine) {
      for (String line : lineOrMuliLine.split("\n")) {
         _lines.add(line);
      }
      return this;
   }

   public UmlSourceBuilder append(List<String> lines) {
      for (String line : lines) {
         _lines.add(line);
      }
      return this;
   }

   public UmlSourceBuilder append(StringReader stringReader) throws IOException {
      final LineNumberReader reader = new LineNumberReader(stringReader);
      String line = reader.readLine();
      while (line != null) {
         _lines.add(line);
         line = reader.readLine();
      }
      return this;
   }

   @SuppressWarnings("unchecked")
   public UmlSourceBuilder append(InputStream stream) throws IOException {
      return append(IOUtils.readLines(stream));
   }

   public UmlSource build() {
      if (_diagramType != null) {
         append(getEndTag());
      }
      return new UmlSource(_lines);
   }

   @Override
   public String toString() {
      return "UmlSourceBuilder [_lines=" + _lines + "]";
   }

   private String getStartTag() {
      return "@start" + _diagramType.name().toLowerCase(Locale.US);
   }

   private String getEndTag() {
      return "@end" + _diagramType.name().toLowerCase(Locale.US);
   }

}
