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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.HexDump;
import org.junit.Test;

/**
 * HexDumpTest.
 */
public class HexDumpTest {

   private static final String EOL = System.getProperty("line.separator");

   @Test
   public void testHexDump() throws Exception {
      final String foo = "@plantuml\nclass Test\n\n";
      byte[] bytes = foo.getBytes("UTF-8");
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      HexDump.dump(bytes, 0, baos, 0);

      final String expected = new StringBuilder()
            .append("00000000 40 70 6C 61 6E 74 75 6D 6C 0A 63 6C 61 73 73 20 @plantuml.class ")
            .append(EOL)
            .append("00000010 54 65 73 74 0A 0A                               Test..")
            .append(EOL)
            .toString();

      assertEquals(expected, baos.toString("UTF-8"));
   }
}
