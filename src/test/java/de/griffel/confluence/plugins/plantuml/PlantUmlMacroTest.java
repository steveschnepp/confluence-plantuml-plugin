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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import net.sourceforge.plantuml.DiagramType;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.google.common.collect.ImmutableMap;

import de.griffel.confluence.plugins.plantuml.PlantUmlMacroParams.Param;
import de.griffel.confluence.plugins.plantuml.preprocess.PageContextMock;

/**
 * Testing {@link de.griffel.confluence.plugins.plantuml.PlantUmlMacro}.
 * 
 * This unit test requires Graphviz.
 */
public class PlantUmlMacroTest {
   private static final String SYSTEM_NEWLINE = System.getProperty("line.separator");

   private final PluginAccessor _pluginAccessor = Mockito.mock(PluginAccessor.class);
   private final Plugin _plugin = Mockito.mock(Plugin.class);
   private final PluginInformation _pluginInfo = Mockito.mock(PluginInformation.class);

   @Before
   public void setup() {
      Mockito.when(_pluginAccessor.getPlugin(PlantUmlPluginInfo.PLUGIN_KEY)).thenReturn(_plugin);
      Mockito.when(_plugin.getPluginInformation()).thenReturn(_pluginInfo);
      Mockito.when(_pluginInfo.getVersion()).thenReturn("1.x");
      Mockito.when(_pluginInfo.getVendorName()).thenReturn("Vendor");
      Mockito.when(_pluginInfo.getVendorUrl()).thenReturn("URL");
      Mockito.when(_pluginInfo.getDescription()).thenReturn("blabla");
   }

   @Test
   public void basic() throws Exception {
      Assume.assumeNotNull(GraphvizUtils.getDotExe());
      final MockExportDownloadResourceManager resourceManager = new MockExportDownloadResourceManager();
      resourceManager.setDownloadResourceWriter(new MockDownloadResourceWriter());
      final PlantUmlMacro macro = new PlantUmlMacro(resourceManager, null, new MockSettingsManager(), _pluginAccessor);
      final Map<Param, String> macroParams = Collections.singletonMap(PlantUmlMacroParams.Param.title, "Sample Title");
      final String macroBody = "A <|-- B\nurl for A is [[Home]]";
      final String result = macro.execute(macroParams, macroBody, new PageContextMock());
      StringBuilder sb = new StringBuilder();
      sb.append("<map id=\"unix\" name=\"unix\">");
      sb.append(SYSTEM_NEWLINE);
      sb.append("<area shape=\"rect\" id=\"node1\" ");
      sb.append("href=\"http://localhost:8080/confluence/display/PUML/Home\" ");
      sb.append("title=\"http://localhost:8080/confluence/display/PUML/Home\" ");
      sb.append("alt=\"\" coords=\"5,5,80,69\"/>");
      sb.append(SYSTEM_NEWLINE);
      sb.append("</map><span class=\"image-wrap\" style=\"\">");
      sb.append("<img usemap=\"#unix\" src='junit/resource.png'/></span>");
      Assert.assertEquals(
            sb.toString(),
            result);
      final ByteArrayOutputStream out = (ByteArrayOutputStream) resourceManager.getResourceWriter(null, null, null)
            .getStreamForWriting();
      Assert.assertTrue(out.toByteArray().length > 0); // file size depends on installation of graphviz
      IOUtils.write(out.toByteArray(), new FileOutputStream("target/junit-basic.png"));
   }

   @Test
   public void ditaa() throws Exception {
      final MockExportDownloadResourceManager resourceManager = new MockExportDownloadResourceManager();
      resourceManager.setDownloadResourceWriter(new MockDownloadResourceWriter());
      final PlantUmlMacro macro = new PlantUmlMacro(resourceManager, null, new MockSettingsManager(), _pluginAccessor);
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
      Assert.assertEquals(
            "<span class=\"image-wrap\" style=\"display: block; text-align: center;\"><img src='junit/resource.png' " +
                  "border=3/></span>",
            result);
      final ByteArrayOutputStream out = (ByteArrayOutputStream) resourceManager.getResourceWriter(null, null, null)
            .getStreamForWriting();
      Assert.assertTrue(out.toByteArray().length > 0); // file size depends on installation of graphviz
      IOUtils.write(out.toByteArray(), new FileOutputStream("target/junit-ditaat.png"));
   }

   @Test
   public void testVersionInfo() throws Exception {
      Assert.assertTrue("@startuml\nversion\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      Assert.assertTrue("@startuml\nabout\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      Assert.assertTrue("@startuml\rversion\r@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
      Assert.assertTrue("@startuml\r\nversion\r\n@enduml\n".matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX));
   }

   static class MockExportDownloadResourceManager implements WritableDownloadResourceManager {

      private DownloadResourceWriter downloadResourceWriter;

      public DownloadResourceReader getResourceReader(String arg0, String arg1, Map arg2)
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
         this.downloadResourceWriter = downloadResourceWriter;
      }

      public DownloadResourceWriter getDownloadResourceWriter() {
         return downloadResourceWriter;
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

   static class MockSettingsManager implements SettingsManager {

      public GlobalDescription getGlobalDescription() {
         throw new UnsupportedOperationException();
      }

      public Settings getGlobalSettings() {
         return new Settings() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getBaseUrl() {
               return "http://localhost:8080/confluence";
            }
         };
      }

      public Serializable getPluginSettings(String arg0) {
         throw new UnsupportedOperationException();
      }

      public SpaceSettings getSpaceSettings(String arg0) {
         throw new UnsupportedOperationException();
      }

      public void updateGlobalDescription(GlobalDescription arg0) {
         throw new UnsupportedOperationException();
      }

      public void updateGlobalSettings(Settings arg0) {
         throw new UnsupportedOperationException();
      }

      public void updatePluginSettings(String arg0, Serializable arg1) {
         throw new UnsupportedOperationException();
      }

      public void updateSpaceSettings(SpaceSettings arg0) {
         throw new UnsupportedOperationException();
      }

   }
}
