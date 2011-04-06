package de.griffel.confluence.plugins.plantuml;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.BlockUmlBuilder;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.preproc.Defines;

import org.apache.log4j.Logger;

import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.collect.Lists;

public class PlantUmlMacro extends BaseMacro {

   private static final Logger logger = Logger.getLogger(PlantUmlMacro.class);

   private final WritableDownloadResourceManager _writeableDownloadResourceManager;

   public PlantUmlMacro(WritableDownloadResourceManager writeableDownloadResourceManager) {
      _writeableDownloadResourceManager = writeableDownloadResourceManager;
   }

   @Override
   public boolean isInline() {
      return false;
   }

   public boolean hasBody() {
      return true;
   }

   public RenderMode getBodyRenderMode() {
      return RenderMode.NO_RENDER;
   }

   public String execute(@SuppressWarnings("rawtypes") Map params, String body, RenderContext renderContext)
         throws MacroException {

      final DownloadResourceWriter resourceWriter = _writeableDownloadResourceManager.getResourceWriter(
            AuthenticatedUserThreadLocal.getUsername(), "plantuml", "png");

      final String umlBlock = toUmlBlock(body);
      final List<String> config = new PlantUmlConfigBuilder().build(new PlantUmlMacroParams(params));
      final SourceStringReader reader = new SourceStringReader(new Defines(), umlBlock, config);
      try {
         reader.generateImage(resourceWriter.getStreamForWriting());
      } catch (IOException e) {
         throw new MacroException(e);
      }

      final StringBuilder sb = new StringBuilder();
      sb.append("<img src='");
      sb.append(resourceWriter.getResourcePath());
      sb.append("'/>");
      final String result = sb.toString();
      return result.toString();
   }

   static String toUmlBlock(String body) {
      final String umlBlock;
      if (BlockUmlBuilder.isArobaseStartuml(body)) {
         umlBlock = body;
      } else {
         final StringBuilder sb = new StringBuilder();
         sb.append("@startuml ");
         sb.append("\n");
         sb.append(body);
         sb.append("\n");
         sb.append("@enduml");
         umlBlock = sb.toString();
      }
      if (logger.isDebugEnabled()) {
         logger.debug("Using UML block " + umlBlock);
      }
      return umlBlock;
   }

   public static class PlantUmlConfigBuilder {
      final List<String> _config = Lists.newArrayList();

      public List<String> build(PlantUmlMacroParams params) {
         appendTitle(params.getTitle());
         return build();
      }

      public void appendTitle(String title) {
         if (title != null) {
            _config.add("title " + title);
         }
      }

      public List<String> build() {
         return Collections.unmodifiableList(_config);
      }
   }

}
