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

import com.atlassian.confluence.renderer.PageContext;

/**
 * ConfluenceLinkTest.
 */
public class ConfluenceLinkTest {
   private static final String DEFAULT_SPACE_KEY = "DFL-SPACE-KEY";
   private static final String DEFAULT_PAGE_TITLE = "Default Page Title - JUnit";

   @Test
   public void testSimple() {
      // pagetitle
      checkResult(DEFAULT_SPACE_KEY, "foo bar buz", null, "foo bar buz");
      // spacekey:pagetitle
      checkResult("spacekey", "pagetitle", null, "spacekey:pagetitle");
      // spacekey:pagetitle^attachment.ext
      checkResult("spacekey", "pagetitle", "attachment.ext", "spacekey:pagetitle^attachment.ext");
      // pagetitle^attachment.ext
      checkResult(DEFAULT_SPACE_KEY, "pagetitle", "attachment.ext", "pagetitle^attachment.ext");
      // ^attachment.ext
      checkResult(DEFAULT_SPACE_KEY, DEFAULT_PAGE_TITLE, "attachment.ext", "^attachment.ext");
   }

   private void checkResult(String expectedSpaceKey, String expectedPageTitle, String expectedAttachmentName,
         String link) {
      final PageContext context = new MyPageContext();
      final ConfluenceLink.Parser parser = new ConfluenceLink.Parser(context);
      final ConfluenceLink confluenceLink = parser.parse(link);
      Assert.assertEquals(expectedSpaceKey, confluenceLink.getSpaceKey());
      Assert.assertEquals(expectedPageTitle, confluenceLink.getPageTitle());
      Assert.assertEquals(expectedAttachmentName, confluenceLink.getAttachmentName());
   }

   private class MyPageContext extends PageContext {

      @Override
      public String getSpaceKey() {
         return DEFAULT_SPACE_KEY;
      }

      @Override
      public String getPageTitle() {
         return DEFAULT_PAGE_TITLE;
      }
   }
}
