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

import org.junit.Assert;
import org.junit.Test;

/**
 * ImageMapTest.
 */
public class ImageMapTest {

   /**
    * Test method for {@link de.griffel.confluence.plugins.plantuml.type.ImageMap#getId()}.
    */
   @Test
   public void testGetId() {
      final StringBuilder sb = new StringBuilder();
      sb.append("<map id=\"unix\" name=\"unix\">\n");
      sb.append("<area shape=\"rect\" id=\"node1\" ");
      sb.append("href=\"http://www.example.com/foo\" ");
      sb.append("title=\"http://www.example.com/foo\" ");
      sb.append("alt=\"\" coords=\"5,5,80,69\">\n");
      sb.append("<area shape=\"rect\" id=\"node2\" ");
      sb.append("href=\"bob.html\" title=\"bob.html\" ");
      sb.append("alt=\"\" coords=\"5,147,80,211\">\n");
      sb.append("</map>\n");
      final String cmap = sb.toString();
      final ImageMap imageMap = new ImageMap(cmap);
      Assert.assertTrue(imageMap.isValid());
      Assert.assertEquals("unix", imageMap.getId());
   }

}
