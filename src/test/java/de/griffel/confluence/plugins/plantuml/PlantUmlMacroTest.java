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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import net.sourceforge.plantuml.DiagramType;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.google.common.collect.ImmutableMap;

import de.griffel.confluence.plugins.plantuml.PlantUmlMacroParams.Param;
import de.griffel.confluence.plugins.plantuml.preprocess.PageContextMock;

/**
 * Testing {@link de.griffel.confluence.plugins.plantuml.PlantUmlMacro}.
 * 
 * This unit test requires GraphViz.
 */
public class PlantUmlMacroTest {
   private static final String NEWLINE = "\n";
   private Mocks mocks;

   @Before
   public void setup() {
      mocks = new Mocks();
   }

   @Test
   public void basic() throws Exception {
      Assume.assumeNotNull(GraphvizUtils.getDotExe());
      Assume.assumeTrue(!GraphvizUtils.dotVersion().startsWith("Error:"));
      final MockExportDownloadResourceManager resourceManager = new MockExportDownloadResourceManager();
      resourceManager.setDownloadResourceWriter(new MockDownloadResourceWriter());
      final PlantUmlMacro macro = new PlantUmlMacro(resourceManager, null, mocks.getSpaceManager(),
            mocks.getSettingsManager(),
            mocks.getPluginAccessor(),
            mocks.getShortcutLinksManager(),
            mocks.getConfigurationManager());
      final Map<Param, String> macroParams = Collections.singletonMap(PlantUmlMacroParams.Param.title, "Sample Title");
      final String macroBody = "A <|-- B\nurl for A is [[Home]]";
      final String result = macro.execute(macroParams, macroBody, new PageContextMock());
      StringBuilder sb = new StringBuilder();
      sb.append("<map id=\"x\" name=\"unix\">");
      sb.append(NEWLINE);
      sb.append("<area shape=\"rect\" id=\"x\" href=\"x\" title=\"x\" alt=\"\" coords=\"x\"/>");
      sb.append(NEWLINE);
      sb.append("</map><div class=\"image-wrap\" style=\"\">");
      sb.append("<img usemap=\"#unix\" src='junit/resource.png' style=\"\" /></div>");
      assertEquals(sb.toString(), result
            // GraphViz Version Specific
            .replaceAll("id=\"[^\"]*\"", "id=\"x\"")
            .replaceFirst("href=\"[^\"]*\"", "href=\"x\"")
            .replaceFirst("title=\"[^\"]*\"", "title=\"x\"")
            .replaceFirst("coords=\"[^\"]*\"", "coords=\"x\""));
      final ByteArrayOutputStream out = (ByteArrayOutputStream) resourceManager.getResourceWriter(null, null, null)
            .getStreamForWriting();
      assertTrue(out.toByteArray().length > 0); // file size depends on installation of graphviz
      IOUtils.write(out.toByteArray(), new FileOutputStream("target/junit-basic.png"));
   }

   @Test
   public void ditaa() throws Exception {
      final MockExportDownloadResourceManager resourceManager = new MockExportDownloadResourceManager();
      resourceManager.setDownloadResourceWriter(new MockDownloadResourceWriter());
      final PlantUmlMacro macro = new PlantUmlMacro(resourceManager, null, mocks.getSpaceManager(),
            mocks.getSettingsManager(),
            mocks.getPluginAccessor(),
            mocks.getShortcutLinksManager(),
            mocks.getConfigurationManager());
      final ImmutableMap<String, String> macroParams = new ImmutableMap.Builder<String, String>().put(
            PlantUmlMacroParams.Param.type.name(), DiagramType.DITAA.name().toLowerCase())
            .put(PlantUmlMacroParams.Param.align.name(), PlantUmlMacroParams.Alignment.center.name())
            .put(PlantUmlMacroParams.Param.border.name(), "3").build();
      final String macroBody = new StringBuilder()
            .append("/--------\\   +-------+\n")
            .append("|cAAA    +---+Version|\n")
            .append("|  Data  |   |   V3  |\n")
            .append("|  Base  |   |cRED{d}|\n")
            .append("|     {s}|   +-------+\n")
            .append("\\---+----/\n").toString();
      final String result = macro.execute(macroParams, macroBody, new PageContextMock());
      assertEquals(
            "<div class=\"image-wrap\" style=\"display: block; text-align: center;\"><img src='junit/resource.png' " +
                  "style=\"border:3px solid black;\" /></div>",
            result);
      final ByteArrayOutputStream out = (ByteArrayOutputStream) resourceManager.getResourceWriter(null, null, null)
            .getStreamForWriting();
      assertTrue(out.toByteArray().length > 0); // file size depends on installation of graphviz
      IOUtils.write(out.toByteArray(), new FileOutputStream("target/junit-ditaat.png"));
   }

   @Test
   public void testVersionInfo() throws Exception {
      assertTrue("@startuml\nversion\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      assertTrue("@startuml\nabout\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      assertTrue("@startuml\rversion\r@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      assertTrue("@startuml\r\nversion\r\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
   }

   @Test
   public void testUnescapeHtml() throws Exception {
      assertEquals("url for \"Referenz Seite\" is [[Referenz Seite#\u00dcberschrift Ebene 3]]",
            PlantUmlMacro.unescapeHtml("url for \"Referenz Seite\" is [[Referenz Seite#&Uuml;berschrift Ebene 3]]"));
   }

   static class MockExportDownloadResourceManager implements WritableDownloadResourceManager {

      private DownloadResourceWriter _downloadResourceWriter;

      public DownloadResourceReader getResourceReader(String arg0, String arg1, @SuppressWarnings("rawtypes") Map arg2)
            throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
         throw new UnsupportedOperationException();
      }

      public boolean matches(String arg0) {
         throw new UnsupportedOperationException();
      }

      public DownloadResourceWriter getResourceWriter(String arg0, String arg1, String arg2) {
         return getDownloadResourceWriter();
      }

      public void setDownloadResourceWriter(DownloadResourceWriter downloadResourceWriter) {
         _downloadResourceWriter = downloadResourceWriter;
      }

      public DownloadResourceWriter getDownloadResourceWriter() {
         return _downloadResourceWriter;
      }

   }

   private static class MockDownloadResourceWriter implements DownloadResourceWriter {
      private final OutputStream buffer = new ByteArrayOutputStream();

      public OutputStream getStreamForWriting() {
         return buffer;
      }

      public String getResourcePath() {
         return "junit/resource.png";
      }
   }

}
