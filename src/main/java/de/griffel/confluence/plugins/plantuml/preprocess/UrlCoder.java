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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * URL en-/decoder is a utility class for {@link URLEncoder} and {@link URLDecoder}.
 */
public final class UrlCoder {
   private UrlCoder() {
      // utility class
   }

   /**
    * The URL encoding that is used by this class: {@value #URL_ENCODING}.
    */
   public static final String URL_ENCODING = "UTF-8";

   /**
    * Returns the URL encoded string of <tt>value</tt>.
    * 
    * @param value the value to URL encode.
    * @return the URL encoded string of <tt>value</tt>.
    */
   public static String encode(final String value) {
      try {
         return URLEncoder.encode(value, URL_ENCODING);
      } catch (UnsupportedEncodingException e) {
         throw new Problem(URL_ENCODING + " encoding not supported?", e);
      }
   }

   /**
    * Returns the URL decoded string of <tt>value</tt>.
    * 
    * @param value the value to URL decode.
    * @return the URL decoded string of <tt>value</tt>.
    */
   public static String decode(final String value) {
      try {
         return URLDecoder.decode(value, URL_ENCODING);
      } catch (UnsupportedEncodingException e) {
         throw new Problem(URL_ENCODING + " encoding not supported?", e);
      }
   }

   private static class Problem extends RuntimeException {
      private static final long serialVersionUID = 1L;

      Problem(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
