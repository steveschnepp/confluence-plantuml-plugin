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

import com.atlassian.gzipfilter.org.apache.commons.lang.StringUtils;

/**
 * Holds the content of a HTML image <map>.
 */
public final class ImageMap {
   private static final String HTML_MAP_MAGIC = "<map id=\"";

   public static final ImageMap NULL = new ImageMap(null);

   private final String cmap;
   private String id; // lazy initialized

   /**
    * Constructs a new instance of an image map.
    * 
    * @param cmap the string representation of the HTML <map> element.
    */
   public ImageMap(String cmap) {
      this.cmap = cmap != null ? cmap.trim() : "";
   }

   /**
    * Returns the <tt>id</tt> attribute of the image map.
    * 
    * @return the <tt>id</tt> attribute of the image map.
    */
   public String getId() {
      if (id == null && isValid()) {
         id = StringUtils.substringBefore(
               StringUtils.substringAfter(cmap, HTML_MAP_MAGIC), "\"");
      }
      return id;
   }

   /**
    * Returns <tt>true</tt> if this image map is valid.
    * 
    * @return <tt>true</tt> if this image map is valid; <tt>false</tt> otherwise.
    */
   public boolean isValid() {
      return cmap.startsWith(HTML_MAP_MAGIC);
   }

   /*
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "ImageMap [id=" + id + ", cmap=" + cmap + "]";
   }

   /**
    * Returns the HTML <map> element as string.
    * 
    * @return the HTML <map> element as string.
    */
   public String toHtmlString() {
      return cmap;
   }

}
