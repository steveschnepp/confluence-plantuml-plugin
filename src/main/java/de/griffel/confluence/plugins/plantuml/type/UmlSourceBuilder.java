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

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfiguration;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationBean;

/**
 * Builder for {@link UmlSource}.
 */
public final class UmlSourceBuilder {
   private final List<String> lines = Lists.newArrayList();
   private final DiagramType diagramType;
   private final boolean dropShadow;
   private final boolean separation;
   private final PlantUmlConfiguration configuration;

   public UmlSourceBuilder(DiagramType diagramType, boolean dropShadow, boolean separation,
         PlantUmlConfiguration configuration) {
      this.diagramType = diagramType;
      this.dropShadow = dropShadow;
      this.separation = separation;
      this.configuration = configuration;

      if (diagramType != null) {
         appendLine(getStartTag());

         if (DiagramType.UML == diagramType) {
            if (!dropShadow) {
               appendLine("skinparam shadowing " + dropShadow);
            }
            if (!configuration.isSvek()) {
               appendLine("skinparam svek off");
            }
            if (configuration.isSetCommonHeader()) {
               append(configuration.getCommonHeader());
            }
         }
      }
   }

   public UmlSourceBuilder() {
      this(null, true, true, new PlantUmlConfigurationBean());
   }

   public UmlSourceBuilder append(String lineOrMuliLine) {
      for (String line : lineOrMuliLine.split("\n")) {
         appendLine(line);
      }
      return this;
   }

   public UmlSourceBuilder append(List<String> lines) {
      for (String line : lines) {
         appendLine(line);
      }
      return this;
   }

   public UmlSourceBuilder append(StringReader stringReader) throws IOException {
      final LineNumberReader reader = new LineNumberReader(stringReader);
      String line = reader.readLine();
      while (line != null) {
         appendLine(line);
         line = reader.readLine();
      }
      return this;
   }

   @SuppressWarnings("unchecked")
   public UmlSourceBuilder append(InputStream stream) throws IOException {
      return append(IOUtils.readLines(stream));
   }

   public UmlSource build() {
      if (diagramType != null) {
         if (DiagramType.UML == diagramType) {
            if (configuration.isSetCommonFooter()) {
               append(configuration.getCommonFooter());
            }
         }
         appendLine(getEndTag());
      }
      return new UmlSource(lines);
   }

   @Override
   public String toString() {
      return "UmlSourceBuilder [_lines=" + lines + "]";
   }

   private void appendLine(String line) {
      if (line != null) {
         // preserve white spaces for ditaa diagrams
         if (diagramType == DiagramType.DITAA) {
            lines.add(line);
         } else {
            final String trimedLine = line.trim();
            if (!trimedLine.isEmpty()) {
               lines.add(trimedLine);
            }
         }
      }
   }

   private String getStartTag() {
      final StringBuilder sb = new StringBuilder();
      sb.append("@start");
      sb.append(diagramType.name().toLowerCase(Locale.US));

      if (DiagramType.DITAA == diagramType) {
         if (!dropShadow) {
            // -S,--no-shadows Turns off the drop-shadow effect.
            sb.append("-S");
         }
         if (!separation) {
            // -E,--no-separation Prevents the separation of common edges of shapes.
            sb.append("-E");
         }
      }
      return sb.toString();
   }

   private String getEndTag() {
      final StringBuilder sb = new StringBuilder();
      sb.append("@end");
      sb.append(diagramType.name().toLowerCase(Locale.US));

      if (DiagramType.DITAA == diagramType) {
         if (!dropShadow) {
            sb.append("-S");
         }
         if (!separation) {
            sb.append("-E");
         }
      }
      return sb.toString();
   }

}
