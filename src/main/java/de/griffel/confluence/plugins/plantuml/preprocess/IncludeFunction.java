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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.UmlSource;

/**
 * 
 * The Include function is responsible to resolve line containing a <tt>include</tt> directive.
 */
public final class IncludeFunction implements LineFunction {
   /**
    * @see net.sourceforge.plantuml.preproc.PreprocessorInclude#includePattern
    */
   private static final Pattern INCLUDE_PATTERN = Pattern.compile("^\\s*!include\\s+\"?([^\"]+)\"?$");

   private final UmlSourceLocator umlSourceLocator;

   /**
    * Constructs a new instance using the given umlSourceLocator
    * 
    * @param umlSourceLocator
    */
   public IncludeFunction(UmlSourceLocator umlSourceLocator) {
      this.umlSourceLocator = umlSourceLocator;
   }

   /**
    * {@inheritDoc}
    */
   public String apply(PreprocessingContext context, String from) throws IOException, PreprocessingException {
      final StringBuilder sb = new StringBuilder();
      final Matcher matcher = INCLUDE_PATTERN.matcher(from);
      if (matcher.find()) {
         final String fileName = matcher.group(1);
         final UmlSource includeSource = umlSourceLocator.get(fileName);
         final PlantUmlPreprocessor subPreprocessor =
               new PlantUmlPreprocessor(includeSource, umlSourceLocator, context);

         sb.append(subPreprocessor.toUmlBlock());

         if (subPreprocessor.hasExceptions()) {
            subPreprocessor.handleExceptions();
         }
      } else {
         sb.append(from);
         sb.append("\n");
      }
      return sb.toString();
   }
}
