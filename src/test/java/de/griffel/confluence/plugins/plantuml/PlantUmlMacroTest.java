package de.griffel.confluence.plugins.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;

import de.griffel.confluence.plugins.plantuml.PlantUmlMacroParams.Param;

/**
 * Testing {@link de.griffel.confluence.plugins.plantuml.PlantUmlMacro}
 */
public class PlantUmlMacroTest {
   @Test
   public void basic() throws Exception {
      final MockExportDownloadResourceManager resourceManager = new MockExportDownloadResourceManager();
      resourceManager.setDownloadResourceWriter(new MockDownloadResourceWriter());
      final PlantUmlMacro macro = new PlantUmlMacro(resourceManager);
      final Map<Param, String> macroParams = Collections.singletonMap(PlantUmlMacroParams.Param.title, "Sample Title");
      final String macroBody = "A <|-- B";
      final String result = macro.execute(macroParams, macroBody, null);
      Assert.assertEquals("<img src='junit/resource.png'/>", result);
      final ByteArrayOutputStream out = (ByteArrayOutputStream) resourceManager.getResourceWriter(null, null, null)
            .getStreamForWriting();
      Assert.assertTrue(out.toByteArray().length > 0); // file size depends on installation of graphviz
      IOUtils.write(out.toByteArray(), new FileOutputStream("target/junit.png"));
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
}
