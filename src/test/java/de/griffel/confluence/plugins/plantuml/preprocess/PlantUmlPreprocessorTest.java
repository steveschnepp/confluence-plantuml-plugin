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

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.spaces.SpaceManager;
import com.google.common.collect.ImmutableList;
import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.core.UmlSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

public class PlantUmlPreprocessorTest {
   @Test
   public void testInlining() throws Exception {
      final UmlSource umlSource = new UmlSource(
            ImmutableList.<CharSequence2>of(
                  new CharSequence2Impl("!include x", null),
                  new CharSequence2Impl("buz", null),
                  new CharSequence2Impl("eof", null)), true);

      Assert.assertEquals("foo\nbar\nbuz\neof\n",
            new PlantUmlPreprocessor(umlSource, new UmlSourceLocator() {
               public UmlSource get(String name) {
                  return new UmlSource(ImmutableList.<CharSequence2>of(
                        new CharSequence2Impl("foo", null),
                        new CharSequence2Impl("bar", null)), true);
               }
            }, new PreprocessingContext() {

               public PageContext getPageContext() {
                  throw new NoSuchMethodError();
               }

               public String getBaseUrl() {
                  return "http:://localhost:8080/confluence";
               }

               public SpaceManager getSpaceManager() {
                  return Mockito.mock(SpaceManager.class);
               }

               public PageManager getPageManager() {
                  return Mockito.mock(PageManager.class);
               }

               public Map<String, ShortcutLinkConfig> getShortcutLinks() {
                  return null;
               }

               public PageAnchorBuilder getPageAnchorBuilder() {
                  return new PageAnchorBuilder();
               }

            }).toUmlBlock());
   }
}
