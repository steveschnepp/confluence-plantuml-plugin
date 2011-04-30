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

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.UmlSource;

import org.apache.log4j.Logger;

public class PlantUmlPreprocessor {
   private final Logger logger = Logger.getLogger(PlantUmlPreprocessor.class);

   /**
    * @see net.sourceforge.plantuml.preproc.PreprocessorInclude#includePattern
    */
   private static final Pattern INCLUDE_PATTERN = Pattern.compile("^\\s*!include\\s+\"?([^\"]+)\"?$");

   private final UmlSource _umlSource;
   private final IncludeFileHandler _includeFileHandler;

   public PlantUmlPreprocessor(UmlSource umlSource, IncludeFileHandler includeFileHandler) throws IOException {
      this(umlSource, null, includeFileHandler);
   }

   private PlantUmlPreprocessor(UmlSource umlSource, PlantUmlPreprocessor parent, IncludeFileHandler includeFileHandler)
         throws IOException {
      _umlSource = umlSource;
      _includeFileHandler = includeFileHandler;
   }

   public String toUmlBlock() throws IOException {
      final StringBuilder sb = new StringBuilder();

      for (Iterator<String> iterator = _umlSource.iterator(); iterator.hasNext();) {
         final String line = iterator.next();

         final Matcher matcher = INCLUDE_PATTERN.matcher(line);
         if (matcher.find()) {
            final String fileName = matcher.group(1);
            logger.info("fileName: " + fileName);
            final UmlSource includeSource = _includeFileHandler.resolve(fileName);
            sb.append(new PlantUmlPreprocessor(includeSource, _includeFileHandler).toUmlBlock());
         } else {
            sb.append(line);
            sb.append("\n");
         }
      }
      return sb.toString();
   }

   /**
    * @return the _includeFileHandler
    */
   public IncludeFileHandler getIncludeFileHandler() {
      return _includeFileHandler;
   }

   public interface IncludeFileHandler {
      UmlSource resolve(String name);
   }
}
