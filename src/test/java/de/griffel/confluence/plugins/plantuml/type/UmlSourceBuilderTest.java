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

import java.io.StringReader;

import net.sourceforge.plantuml.core.DiagramType;

import org.junit.Assert;
import org.junit.Test;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationBean;

/**
 * UmlSourceBuilderTest.
 */
public class UmlSourceBuilderTest {
   private static final String NEWLINE = "\r\n";

   @Test
   public void testPuml58() throws Exception {
      final UmlSourceBuilder builder =
            new UmlSourceBuilder(DiagramType.DOT, false, false, new PlantUmlConfigurationBean());
      final String body =
            NEWLINE + "digraph deschedule_app_confusion {" + NEWLINE;
      builder.append(new StringReader(body));
      Assert.assertEquals("@startdot" + NEWLINE + "digraph deschedule_app_confusion {" + NEWLINE + "@enddot" + NEWLINE,
            builder.build().getPlainString());
   }

   @Test
   public void testPuml77() throws Exception {
      final UmlSourceBuilder builder =
            new UmlSourceBuilder(DiagramType.UML, false, false, new PlantUmlConfigurationBean());
      final String body =
            "class Test\n\u00a0\n";
      builder.append(new StringReader(body));
      Assert.assertEquals("@startuml" + NEWLINE + "skinparam shadowing false" + NEWLINE + "class Test" + NEWLINE
            + "@enduml" + NEWLINE, builder.build().getPlainString());
   }
}
