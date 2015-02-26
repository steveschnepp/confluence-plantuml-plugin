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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import de.griffel.confluence.plugins.plantuml.type.GraphBuilder.NodeShape;
import de.griffel.confluence.plugins.plantuml.type.GraphBuilder.NodeStyle;

/**
 * JUnit Test for {@link GraphBuilder}.
 */
public class GraphBuilderTest {
   private final String NEWLINE = System.getProperty("line.separator");

   @Test
   public void testDefault() {
      assertEquals(
            "digraph g {" + NEWLINE + "edge [arrowsize=\"0.8\"];" + NEWLINE
                  + "node [shape=\"rect\", style=\"filled\", fillcolor=\"lightyellow\", "
                  + "fontname=\"Verdana\", fontsize=\"9\"];" + NEWLINE
                  + "Foo -> Bar" + NEWLINE + "Bar -> Buz" + NEWLINE + "}" + NEWLINE,
            new GraphBuilder()
                  .appendGraph("Foo -> Bar")
                  .appendGraph("Bar -> Buz")
                  .build());
   }

   @Test
   public void testStyling() {
      assertEquals(
            "digraph g {" + NEWLINE + "edge [arrowsize=\"0.9\"];" + NEWLINE
                  + "node [shape=\"note\", style=\"dotted\", fillcolor=\"lightgreen\", "
                  + "fontname=\"Times New Roman\", fontsize=\"10\"];" + NEWLINE
                  + "Foo -> Bar" + NEWLINE + "Bar -> Buz" + NEWLINE + "}" + NEWLINE,
            new GraphBuilder()
                  .withEdgeArrowSize(new BigDecimal("0.9"))
                  .withNodeShape(NodeShape.note)
                  .withNodeStyle(NodeStyle.dotted)
                  .withNodeFillColor("lightgreen")
                  .withNodeFontname("Times New Roman")
                  .withNodeFontsize(BigDecimal.TEN)
                  .appendGraph("Foo -> Bar")
                  .appendGraph("Bar -> Buz")
                  .build());
   }
}
