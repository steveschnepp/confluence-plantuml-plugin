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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.core.UmlSource;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.sourceforge.plantuml.version.IteratorCounter2;

public final class PlantUmlPreprocessor {

   private final UmlSource umlSource;
   private final UmlSourceLocator umlSourceLocator;
   private final PreprocessingContext context;
   private final List<PreprocessingException> errors = Lists.newArrayList();

   public PlantUmlPreprocessor(UmlSource umlSource, UmlSourceLocator includeFileHandler, PreprocessingContext context)
         throws IOException {
      this.umlSource = umlSource;
      umlSourceLocator = includeFileHandler;
      this.context = context;
   }

   public String toUmlBlock() throws IOException {
      final StringBuilder sb = new StringBuilder();
      final StringFunctions functions = StringFunctions.builder()
            .add(new IncludeFunction(umlSourceLocator))
            .add(new UrlReplaceFunction())
            .build();

      for (IteratorCounter2 iterator = umlSource.iterator2(); iterator.hasNext();) {
         final String line = iterator.next().toString2();
         try {
            sb.append(functions.apply(context, line));
         } catch (PreprocessingException e) {
            errors.add(e);
         }
      }
      return sb.toString();
   }

   /**
    * @return the _includeFileHandler
    */
   public UmlSourceLocator getIncludeFileHandler() {
      return umlSourceLocator;
   }

   public void handleExceptions() throws PreprocessingException {
      if (hasExceptions()) {
         if (errors.size() == 1) {
            throw errors.iterator().next();
         } else {
            throw new PreprocessingException(null, String.valueOf(Iterables.transform(errors,
                  new Function<PreprocessingException, String>() {
                     public String apply(PreprocessingException from) {
                        return from.getDetails();
                     }
                  })));
         }
      }
   }

   /**
    * Returns {@code true} if during the pre-processoing at least one error occurred.
    * 
    * @return {@code true} if during the pre-processoing at least one error occurred; {@code false} otherwise.
    */
   public boolean hasExceptions() {
      return !errors.isEmpty();
   }

   /**
    * Returns a immutable list of all errors that are occurred during the pre-processing.
    * 
    * @return a immutable list of all errors that are occurred during the pre-processing.
    */
   public List<PreprocessingException> getExceptions() {
      return Collections.unmodifiableList(errors);
   }
}
