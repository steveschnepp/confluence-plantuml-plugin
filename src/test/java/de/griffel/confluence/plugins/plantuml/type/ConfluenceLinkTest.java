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

import de.griffel.confluence.plugins.plantuml.preprocess.ExternalUrlRenderer;

/**
 * ConfluenceLinkTest.
 */
public class ConfluenceLinkTest {
   private static final String DEFAULT_SPACE_KEY = "DFL-SPACE-KEY";
   private static final String DEFAULT_PAGE_TITLE = "Default Page Title - JUnit";
   private static final String BASE_URL = "http://foo.com/bar";

   @Test
   public void testToString() {
      Assert.assertEquals("spaceKey:pageTitle", new ConfluenceLink("spaceKey", "pageTitle", null, null).toString());
      Assert.assertEquals("spacekey:pageTitle^attachment", new ConfluenceLink("spacekey", "pageTitle",
            "attachment", null).toString());
      Assert.assertEquals("spacekey:pageTitle#section", new ConfluenceLink("spacekey", "pageTitle",
            null, "section").toString());
   }

   @Test
   public void testSimple() {
      // pagetitle
      checkResult(DEFAULT_SPACE_KEY, "foo bar buz", null, null, "foo bar buz");
      // spacekey:pagetitle
      checkResult("spacekey", "pagetitle", null, null, "spacekey:pagetitle");
      // spacekey:pagetitle#section
      checkResult("spacekey", "pagetitle", null, "section", "spacekey:pagetitle#section");
      // spacekey:pagetitle^attachment.ext
      checkResult("spacekey", "pagetitle", "attachment.ext", null, "spacekey:pagetitle^attachment.ext");
      // pagetitle^attachment.ext
      checkResult(DEFAULT_SPACE_KEY, "pagetitle", "attachment.ext", null, "pagetitle^attachment.ext");
      // pagetitle#section
      checkResult(DEFAULT_SPACE_KEY, "foo bar buz", null, "section", "foo bar buz#section");
      // ^attachment.ext
      checkResult(DEFAULT_SPACE_KEY, DEFAULT_PAGE_TITLE, "attachment.ext", null, "^attachment.ext");
      // #section
      checkResult(DEFAULT_SPACE_KEY, DEFAULT_PAGE_TITLE, null, "section", "#section");
   }

   private void checkResult(String expectedSpaceKey, String expectedPageTitle, String expectedAttachmentName,
         String expectedSection, String link) {
      final PageContext context = new MyPageContext();
      final ConfluenceLink.Parser parser = new ConfluenceLink.Parser(context);
      final ConfluenceLink confluenceLink = parser.parse(link);
      Assert.assertEquals(expectedSpaceKey, confluenceLink.getSpaceKey());
      Assert.assertEquals(expectedPageTitle, confluenceLink.getPageTitle());
      Assert.assertEquals(expectedAttachmentName, confluenceLink.getAttachmentName());
      Assert.assertEquals(expectedSection, confluenceLink.getFragment());
   }

   @Test
   public void testToUrl() throws Exception {
      checkUrl("FOO", "PageTitle", null, BASE_URL + "/display/FOO/PageTitle");
      checkUrl("FOO", "Page with spaces", null, BASE_URL + "/display/FOO/Page+with+spaces");
      checkUrl("FOO", "2011/12/02/Blog Post", null, BASE_URL + "/display/FOO/2011/12/02/Blog+Post");
      checkUrl("FOO", "PageTitle", "Section", BASE_URL + "/display/FOO/PageTitle#PageTitle-Section");
      checkUrl("FOO", "Page Title", "Section x\u00e4\u00f6\u00fcx", BASE_URL
            + "/display/FOO/Page+Title#PageTitle-Sectionx%C3%A4%C3%B6%C3%BCx");
   }

   private void checkUrl(String spaceKey, String pageTitle, String section, String url) {
      final ConfluenceLink link = new ConfluenceLink(spaceKey, pageTitle, null, section);
      Assert.assertEquals(url, new ExternalUrlRenderer(BASE_URL).getHyperlink(link));

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
