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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfiguration;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationBean;

/**
 * Builder for {@link UmlSource}.
 */
public final class UmlSourceBuilder {
   /**
    * NO-BREAK SPACE. (U+00A0, 0xc2a0)
    */
   private static final String NO_BREAK_SPACE = "\u00a0";

   private final List<CharSequence2> lines = Lists.newArrayList();
   private final DiagramType diagramType;
   private final boolean dropShadow;
   private final boolean separation;
   private final PlantUmlConfiguration configuration;
   private boolean isDitaa = false;

   public UmlSourceBuilder(DiagramType diagramType, boolean dropShadow, boolean separation,
         PlantUmlConfiguration configuration) {
      isDitaa = DiagramType.DITAA == diagramType;
      this.diagramType = diagramType;
      this.dropShadow = dropShadow;
      this.separation = separation;
      this.configuration = checkNotNull(configuration);

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
         } else if (DiagramType.SALT == diagramType) {
            appendLine("{");
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
      if ("ditaa".equals(line) && DiagramType.UML == diagramType) {
         isDitaa = true;
      }
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
         } else if (DiagramType.SALT == diagramType) {
            appendLine("}");
         }

         appendLine(getEndTag());
      }
      return new UmlSource(lines, !isDitaa); // DITAA needs literal backslashes
   }

   @Override
   public String toString() {
      return "UmlSourceBuilder [_lines=" + lines + "]";
   }

   private void appendLine(String line) {
      if (line != null) {
         // preserve white spaces for ditaa diagrams
         if (isDitaa) {
            lines.add(new CharSequence2Impl(line, null));
         } else {
            final String trimedLine = line.trim();
            if (!trimedLine.isEmpty() && !trimedLine.equals(NO_BREAK_SPACE)) {
               lines.add(new CharSequence2Impl(trimedLine, null));
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
