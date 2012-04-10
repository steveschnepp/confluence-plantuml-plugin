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

/**
 * Exception thrown during pre-processing.
 */
public class PreprocessingException extends Exception {

   private static final long serialVersionUID = 1L;
   private final String line;

   public PreprocessingException(String line, String message, Throwable cause) {
      super(message, cause);
      this.line = line;
   }

   public PreprocessingException(String line, String message) {
      super(message);
      this.line = line;
   }

   public PreprocessingException(String line, Throwable cause) {
      super(cause);
      this.line = line;
   }

   /**
    * Returns the line where this exception occurred.
    * 
    * @return the line where this exception occurred.
    */
   public final String getLine() {
      return line;
   }

   /**
    * Returns the details error message for the user.
    * 
    * @return the details error message for the user.
    */
   public final String getDetails() {
      final StringBuilder sb = new StringBuilder();
      if (getLine() != null) {
         sb.append("line: '");
         sb.append(getLine());
         sb.append("' ");
      }
      sb.append(getMessage());
      return sb.toString();
   }
}
