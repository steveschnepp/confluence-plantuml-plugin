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
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.preproc.Defines;

import org.apache.commons.io.HexDump;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.PlantUmlMacro.MySourceStringReader.ImageInfo;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfiguration;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;
import de.griffel.confluence.plugins.plantuml.preprocess.PageAnchorBuilder;
import de.griffel.confluence.plugins.plantuml.preprocess.PlantUmlPreprocessor;
import de.griffel.confluence.plugins.plantuml.preprocess.PreprocessingContext;
import de.griffel.confluence.plugins.plantuml.preprocess.PreprocessingException;
import de.griffel.confluence.plugins.plantuml.preprocess.UmlSourceLocator;
import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;
import de.griffel.confluence.plugins.plantuml.type.ImageMap;
import de.griffel.confluence.plugins.plantuml.type.UmlSourceBuilder;

/**
 * The Confluence PlantUML Macro.
 * 
 * @author Michael Griffel
 */
public class PlantUmlMacro extends BaseMacro {
   private static final Logger logger = Logger.getLogger(PlantUmlMacro.class);

   private final WritableDownloadResourceManager writeableDownloadResourceManager;

   private final PageManager pageManager;

   private final SpaceManager spaceManager;

   private final SettingsManager settingsManager;

   private final PluginAccessor pluginAccessor;

   private final ShortcutLinksManager shortcutLinksManager;

   private final PlantUmlConfigurationManager configurationManager;

   private final I18NBeanFactory i18NBeanFactory;

   public PlantUmlMacro(WritableDownloadResourceManager writeableDownloadResourceManager,
         PageManager pageManager, SpaceManager spaceManager, SettingsManager settingsManager,
         PluginAccessor pluginAccessor, ShortcutLinksManager shortcutLinksManager,
         PlantUmlConfigurationManager configurationManager,
         I18NBeanFactory i18NBeanFactory) {
      this.writeableDownloadResourceManager = writeableDownloadResourceManager;
      this.pageManager = pageManager;
      this.spaceManager = spaceManager;
      this.settingsManager = settingsManager;
      this.pluginAccessor = pluginAccessor;
      this.shortcutLinksManager = shortcutLinksManager;
      this.configurationManager = configurationManager;
      this.i18NBeanFactory = i18NBeanFactory;
   }

   @Override
   public final boolean isInline() {
      return false;
   }

   public final boolean hasBody() {
      return true;
   }

   public final RenderMode getBodyRenderMode() {
      return RenderMode.NO_RENDER;
   }

   public PageAnchorBuilder createPageAnchorBuilder() {
      return new PageAnchorBuilder();
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public String execute(Map params, final String body, final RenderContext renderContext)
         throws MacroException {

      try {
         final String unescapeHtml = unescapeHtml(body);
         return executeInternal(params, unescapeHtml, renderContext);
      } catch (final IOException e) {
         throw new MacroException(e);
      } catch (UnauthorizedDownloadResourceException e) {
         throw new MacroException(e);
      } catch (DownloadResourceNotFoundException e) {
         throw new MacroException(e);
      }
   }

   static String unescapeHtml(final String body) throws IOException {
      final StringWriter sw = new StringWriter();
      StringEscapeUtils.unescapeHtml(sw, body);
      return sw.toString();
   }

   protected final String executeInternal(Map<String, String> params, final String body,
         final RenderContext renderContext)
         throws MacroException, IOException, UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {

      final StopWatch stopWatch = new StopWatch();
      stopWatch.start();

      final PlantUmlMacroParams macroParams = new PlantUmlMacroParams(params);

      if (!(renderContext instanceof PageContext)) {
         throw new MacroException("This macro can only be used in Confluence pages. (ctx="
               + renderContext.getClass().getName() + ")");
      }

      final PageContext pageContext = (PageContext) renderContext;
      final UmlSourceLocator umlSourceLocator = new UmlSourceLocatorConfluence(pageContext);
      final PreprocessingContext preprocessingContext = new MyPreprocessingContext(pageContext);

      final DiagramType diagramType = macroParams.getDiagramType();
      final boolean dropShadow = macroParams.getDropShadow();
      final boolean separation = macroParams.getSeparation();
      final PlantUmlConfiguration configuration = configurationManager.load();
      final UmlSourceBuilder builder =
            new UmlSourceBuilder(diagramType, dropShadow, separation, configuration).append(new StringReader(body));
      final PlantUmlPreprocessor preprocessor =
            new PlantUmlPreprocessor(builder.build(), umlSourceLocator, preprocessingContext);
      final String umlBlock = preprocessor.toUmlBlock();

      final String result = render(umlBlock, pageContext, macroParams, preprocessor);

      stopWatch.stop();
      logger.info(String.format("Rendering %s diagram on page %s:%s took %d ms.", diagramType,
            pageContext.getSpaceKey(), pageContext.getPageTitle(), stopWatch.getTime()));

      return result;
   }

   private String render(final String umlBlock, final PageContext pageContext, final PlantUmlMacroParams macroParams,
         final PlantUmlPreprocessor preprocessor) throws IOException,
         UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {

      final FileFormat fileFormat = macroParams.getFileFormat(pageContext);

      final List<String> config = new PlantUmlConfigBuilder().build(macroParams);
      final MySourceStringReader reader = new MySourceStringReader(new Defines(), umlBlock, config);

      final StringBuilder sb = new StringBuilder();

      if (preprocessor.hasExceptions()) {
         sb.append("<span class=\"error\">");
         for (PreprocessingException exception : preprocessor.getExceptions()) {
            sb.append("<span class=\"error\">");
            sb.append("plantuml: ");
            sb.append(exception.getDetails());
            sb.append("</span><br/>");
         }
         sb.append("</span>");
      }

      while (reader.hasNext()) {
         final DownloadResourceWriter resourceWriter = writeableDownloadResourceManager.getResourceWriter(
               AuthenticatedUserThreadLocal.getUsername(), "plantuml", fileFormat.getFileSuffix());

         final ImageInfo imageInfo = reader.renderImage(resourceWriter.getStreamForWriting(), fileFormat);
         final ImageMap cmap = imageInfo.getImageMap();

         if (cmap.isValid()) {
            sb.append(cmap.toHtmlString());
         }

         if (umlBlock.matches(PlantUmlPluginInfo.PLANTUML_VERSION_INFO_REGEX)) {
            sb.append(new PlantUmlPluginInfo(pluginAccessor, i18NBeanFactory.getI18NBean()).toHtmlString());
         }

         final DownloadResourceInfo resourceInfo;
         if (macroParams.getExportName() != null && !preprocessor.hasExceptions()) {
            resourceInfo = attachImage(pageContext.getEntity(), macroParams, imageInfo, fileFormat, resourceWriter);
         } else {
            resourceInfo = new DefaultDownloadResourceInfo(writeableDownloadResourceManager, resourceWriter);
         }

         if (FileFormat.SVG == fileFormat) {
            final StringWriter sw = new StringWriter();
            IOUtils.copy(resourceInfo.getStreamForReading(), sw);
            sb.append(sw.getBuffer());
         } else /* PNG */{
            sb.append("<span class=\"image-wrap\" style=\"" + macroParams.getAlignment().getCssStyle() + "\">");
            sb.append("<img");
            if (cmap.isValid()) {
               sb.append(" usemap=\"#");
               sb.append(cmap.getId());
               sb.append("\"");
            }
            sb.append(" src='");
            sb.append(resourceInfo.getDownloadPath());
            sb.append("'");
            sb.append(macroParams.getImageStyle());
            sb.append("/>");
            sb.append("</span>");
         }

      }

      if (macroParams.isDebug()) {
         sb.append("<div class=\"puml-debug\">");
         sb.append("<pre>");
         final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         HexDump.dump(umlBlock.getBytes("UTF-8"), 0, baos, 0);
         sb.append(baos.toString()); // HexDump class writer bytes with JVM default encoding
         sb.append("</pre>");
         sb.append("</div>");
      }

      return sb.toString();
   }
   
   private DownloadResourceInfo attachImage(final ContentEntityObject page, final PlantUmlMacroParams macroParams,
         ImageInfo imageInfo, final FileFormat fileFormat, final DownloadResourceWriter resourceWriter)
         throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException, IOException {

      final String attachmentName;
      if (imageInfo.isSplitImage()) {
         attachmentName = macroParams.getExportName() + "-" + imageInfo.getIndex() + fileFormat.getFileSuffix();
      } else {
         attachmentName = macroParams.getExportName() + fileFormat.getFileSuffix();
      }
      Attachment attachment = pageManager.getAttachmentManager().getAttachment(page, attachmentName);

      final Attachment previousVersion;
      if (attachment == null) {
         previousVersion = null;
         attachment = new Attachment();
         attachment.setFileName(attachmentName);
         attachment.setContentType("image/" + fileFormat.name().toLowerCase());
         attachment.setComment("PlantUML Diagram (generated)");
      } else {
         try {
            previousVersion = (Attachment) attachment.clone();
         } catch (CloneNotSupportedException e) {
            throw new InfrastructureException(e);
         }
      }

      final DownloadResourceReader resourceReader =
            writeableDownloadResourceManager.getResourceReader(AuthenticatedUserThreadLocal.getUsername(),
                  resourceWriter.getResourcePath(), Collections.emptyMap());

      if (previousVersion == null
            || previousVersion.getFileSize() != resourceReader.getContentLength()
            || !IOUtils.contentEquals(previousVersion.getContentsAsStream(), resourceReader.getStreamForReading())) {
         attachment.setFileSize(resourceReader.getContentLength());
         page.addAttachment(attachment);
         pageManager.getAttachmentManager().saveAttachment(attachment, previousVersion,
               resourceReader.getStreamForReading());

         logger.debug("Saved image as attachment " + attachmentName);
      }
      return new AttachmentDownloadResourceInfo(settingsManager.getGlobalSettings().getBaseUrl(), attachment);
   }

   private final class MyPreprocessingContext implements PreprocessingContext {
      private final PageContext pageContext;

      /**
       * {@inheritDoc}
       */
      private MyPreprocessingContext(PageContext pageContext) {
         this.pageContext = pageContext;
      }

      /**
       * Returns the base URL from the global settings.
       * 
       * @return the base URL from the global settings.
       */
      public String getBaseUrl() {
         final String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
         return baseUrl;
      }

      /**
       * {@inheritDoc}
       */
      public PageContext getPageContext() {
         return pageContext;
      }

      public SpaceManager getSpaceManager() {
         return spaceManager;
      }

      /**
       * {@inheritDoc}
       */
      public PageManager getPageManager() {
         return pageManager;
      }

      /**
       * {@inheritDoc}
       */
      public Map<String, ShortcutLinkConfig> getShortcutLinks() {
         return shortcutLinksManager.getShortcutLinks();
      }

      /**
       * {@inheritDoc}
       */
      public PageAnchorBuilder getPageAnchorBuilder() {
         return createPageAnchorBuilder();
      }
   }

   /**
    * Gets the UML source either from a Confluence page or from an attachment.
    */
   private final class UmlSourceLocatorConfluence implements UmlSourceLocator {
      private final PageContext pageContext;

      /**
       * @param pageContext
       */
      private UmlSourceLocatorConfluence(PageContext pageContext) {
         this.pageContext = pageContext;
      }

      public UmlSource get(String name) throws IOException {
         final ConfluenceLink.Parser parser = new ConfluenceLink.Parser(pageContext, spaceManager, pageManager);
         final ConfluenceLink confluenceLink = parser.parse(name);

         if (logger.isDebugEnabled()) {
            logger.debug("Link '" + name + "' -> " + confluenceLink);
         }

         final Page page = pageManager.getPage(confluenceLink.getSpaceKey(), confluenceLink.getPageTitle());
         // page cannot be null since it is validated before
         if (confluenceLink.hasAttachmentName()) {
            final Attachment attachment =
                  pageManager.getAttachmentManager().getAttachment(page, confluenceLink.getAttachmentName());
            if (attachment == null) {
               throw new IOException("Cannot find attachment '" + confluenceLink.getAttachmentName()
                     + "' on page '" + confluenceLink.getPageTitle()
                     + "' in space '" + confluenceLink.getSpaceKey() + "'");
            }
            return new UmlSourceBuilder().append(attachment.getContentsAsStream()).build();

         } else {
            return new UmlSourceBuilder().append(page.getBodyAsStringWithoutMarkup()).build();
         }
      }
   }

   /**
    * Extension to {@link SourceStringReader} to add the function to get the image map for the diagram.
    */
   public static class MySourceStringReader extends SourceStringReader {
      private static final Random RANDOM = new Random();
      private final BlockUml blockUml;
      private final Diagram system;
      private int index = 0;

      /**
       * {@inheritDoc}
       */
      public MySourceStringReader(Defines defines, String source, List<String> config) {
         super(defines, source, config);
         blockUml = getBlocks().iterator().next();
         system = blockUml.getDiagram();
      }

      public boolean hasNext() {
         return index < system.getNbImages();
      }

      public final ImageInfo renderImage(OutputStream outputStream, FileFormat format) throws IOException {
         final ImageData imageData = system.exportDiagram(outputStream, index, new FileFormatOption(format));

         final ImageMap imageMap;
         if (imageData.containsCMapData()) {
            final String randomId = String.valueOf(Math.abs(RANDOM.nextInt()));
            imageMap = new ImageMap(imageData.getCMapData("plantuml" + randomId));
         } else {
            imageMap = ImageMap.NULL;
         }
         return new ImageInfo(imageMap, index++);
      }

      public final class ImageInfo {
         private final ImageMap imageMap;
         private final int index;

         ImageInfo(ImageMap imageMap, int index) {
            this.imageMap = imageMap;
            this.index = index;
         }

         public boolean isSplitImage() {
            return system.getNbImages() > 1;
         }

         public ImageMap getImageMap() {
            return imageMap;
         }

         public int getIndex() {
            return index;
         }
      }
   }

}
