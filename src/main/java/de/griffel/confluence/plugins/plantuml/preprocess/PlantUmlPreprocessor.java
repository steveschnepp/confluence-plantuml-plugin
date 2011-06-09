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
import java.util.Iterator;

import net.sourceforge.plantuml.UmlSource;

import com.atlassian.renderer.v2.macro.MacroException;

public class PlantUmlPreprocessor {

   private final UmlSource _umlSource;
   private final UmlSourceLocator _umlSourceLocator;
   private final PreprocessingContext _context;

   public PlantUmlPreprocessor(UmlSource umlSource, UmlSourceLocator includeFileHandler, PreprocessingContext context)
         throws IOException {
      _umlSource = umlSource;
      _umlSourceLocator = includeFileHandler;
      _context = context;
   }

   public String toUmlBlock() throws IOException, MacroException {
      final StringBuilder sb = new StringBuilder();
      final StringFunctions functions = StringFunctions.builder()
            .add(new IncludeFunction(_umlSourceLocator))
            .add(new UrlReplaceFunction())
            .build();

      for (Iterator<String> iterator = _umlSource.iterator(); iterator.hasNext();) {
         final String line = iterator.next();
         sb.append(functions.apply(_context, line));
      }
      return sb.toString();
   }

   /**
    * @return the _includeFileHandler
    */
   public UmlSourceLocator getIncludeFileHandler() {
      return _umlSourceLocator;
   }

}
