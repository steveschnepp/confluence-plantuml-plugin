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

/**
 * Interface to transform a line during the preprocessing phase.
 */
public interface LineFunction {

   /**
    * Transforms the given line.
    * 
    * @param ctx the preprocessing context
    * @param line the current input line.
    * @return the transformed line.
    * @throws IOException in case of any I/O error.
    * @throws PreprocessingException in case of a problem during pre-processing.
    * 
    */
   String apply(PreprocessingContext ctx, String line) throws IOException, PreprocessingException;
}
