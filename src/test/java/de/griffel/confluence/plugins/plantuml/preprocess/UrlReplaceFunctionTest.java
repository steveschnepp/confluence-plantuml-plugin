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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.griffel.confluence.plugins.plantuml.Mocks;

/**
 * JUnit Test for UrlReplaceFunctionTest.
 */
public class UrlReplaceFunctionTest {

   private Mocks _mocks;

   @Before
   public void setup() {
      _mocks = new Mocks();
   }

   @Test
   public void testSimple() throws Exception {
      checkConfluencekUrl("url for Bob is [[Home]]", "/display/PUML/Home", "PlantUML Space - Home");
      checkConfluencekUrl("url for Bob is [[PUML:Home]]", "/display/PUML/Home", "PlantUML Space - Home");
      checkConfluencekUrl("url for Bob is [[PUML:Home|alias]]", "/display/PUML/Home", "alias");
      checkConfluencekUrl("url for Bob is [[PUML:Home|alias foo bar]]", "/display/PUML/Home", "alias foo bar");
      checExternalUrl("url for Bob is [[/foo/bar.html]]");
      checExternalUrl("url for Bob is [[https://www.example.com/secure]]");
      checExternalUrl("url for Bob is [[http://www.example.com/foo/bar.html]]");
      checExternalUrl("url for Bob is [[http://www.example.com/foo/bar.html|alias]]");
   }

   private void checkConfluencekUrl(String line, String result, String alias) {
      Assert.assertEquals("url for Bob is " + "[[" + _mocks.getBaseUrl() + result + "|" + alias + "]]",
            new UrlReplaceFunction().apply(_mocks.getPreprocessingContext(), line));
   }

   private void checExternalUrl(String line) {
      Assert.assertEquals(line, new UrlReplaceFunction().apply(_mocks.getPreprocessingContext(), line));
   }
}
