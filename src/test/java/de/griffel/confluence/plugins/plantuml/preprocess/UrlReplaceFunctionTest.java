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
import org.mockito.Mockito;

/**
 * JUnit Test for UrlReplaceFunctionTest.
 */
public class UrlReplaceFunctionTest {

   private static final String BASE_URL = "http://localhost:8080/confluence";

   private final PreprocessingContext contextMock = Mockito.mock(PreprocessingContext.class);

   @Before
   public void setup() {
      Mockito.when(contextMock.getBaseUrl()).thenReturn(BASE_URL);
   }

   @Test
   public void testSimple() throws Exception {
      checConfluencekUrl("url for Bob is [[Home]]", "/display/PUML/Home");
      checConfluencekUrl("url for Bob is [[PUML:Home]]", "/display/PUML/Home");
      checConfluencekUrl("url for Bob is [[PUML:Home|alias]]", "/display/PUML/Home|alias");
      checConfluencekUrl("url for Bob is [[PUML:Home|alias foo bar]]", "/display/PUML/Home|alias foo bar");
      checExternalUrl("url for Bob is [[/foo/bar.html]]");
      checExternalUrl("url for Bob is [[https://www.example.com/secure]]");
      checExternalUrl("url for Bob is [[http://www.example.com/foo/bar.html]]");
      checExternalUrl("url for Bob is [[http://www.example.com/foo/bar.html|alias]]");
   }

   private void checConfluencekUrl(String line, String result) {
      Assert.assertEquals("url for Bob is " + "[[" + BASE_URL + result + "]]",
            new UrlReplaceFunction().apply(contextMock, line));
   }

   private void checExternalUrl(String line) {
      Assert.assertEquals(line, new UrlReplaceFunction().apply(contextMock, line));
   }
}
