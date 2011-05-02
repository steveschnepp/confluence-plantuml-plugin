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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.DiagramType;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.UmlSource;
import net.sourceforge.plantuml.preproc.Defines;

import org.apache.log4j.Logger;

import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.collect.Lists;

import de.griffel.confluence.plugins.plantuml.PlantUmlPreprocessor.UmlSourceLocator;

/**
 * The Confluence PlantUML Macro.
 * 
 * @author Michael Griffel
 */
public class PlantUmlMacro extends BaseMacro {
   private final Logger logger = Logger.getLogger(PlantUmlMacro.class);

   private final WritableDownloadResourceManager _writeableDownloadResourceManager;

   private final PageManager _pageManager;

   public PlantUmlMacro(WritableDownloadResourceManager writeableDownloadResourceManager,
         PageManager pageManager) {
      _writeableDownloadResourceManager = writeableDownloadResourceManager;
      _pageManager = pageManager;
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

   public String execute(@SuppressWarnings("rawtypes") final Map params, final String body,
         final RenderContext renderContext)
         throws MacroException {

      final DownloadResourceWriter resourceWriter = _writeableDownloadResourceManager.getResourceWriter(
            AuthenticatedUserThreadLocal.getUsername(), "plantuml", "png");

      final UmlSourceLocator umlSourceLocator;
      if (renderContext instanceof PageContext) {
         final PageContext pageContext = (PageContext) renderContext;
         umlSourceLocator = new UmlSourceLocator() {
            public UmlSource get(String name) throws IOException {
               final ConfluenceLinkParser parser = new ConfluenceLinkParser(pageContext);
               final ConfluenceLink confluenceLink = parser.parse(name);

               if (logger.isDebugEnabled()) {
                  logger.debug("Link '" + name + "' -> " + confluenceLink);
               }

               if (confluenceLink.hasAttachmentName()) {
                  final Page page = _pageManager.getPage(confluenceLink.getSpaceKey(), confluenceLink.getPageTitle());
                  if (page == null) {
                     throw new IOException("Cannot find page '" + confluenceLink.getPageTitle()
                           + "' in space '" + confluenceLink.getSpaceKey() + "'");
                  }
                  final Attachment attachment = page.getAttachmentNamed(confluenceLink.getAttachmentName());
                  if (attachment == null) {
                     throw new IOException("Cannot find attachment '" + confluenceLink.getAttachmentName()
                           + "' on page '" + confluenceLink.getPageTitle()
                           + "' in space '" + confluenceLink.getSpaceKey() + "'");
                  }
                  return new UmlSourceBuilder().append(attachment.getContentsAsStream()).build();

               } else {
                  final Page page = _pageManager.getPage(confluenceLink.getSpaceKey(), confluenceLink.getPageTitle());
                  if (page == null) {
                     throw new IOException("Cannot find page '" + confluenceLink.getPageTitle()
                           + "' in space '" + confluenceLink.getSpaceKey() + "'");
                  }
                  return new UmlSourceBuilder().append(page.getContent()).build();
               }
            }
         };
      } else {
         umlSourceLocator = null;
      }

      final PlantUmlMacroParams macroParams = new PlantUmlMacroParams(params);
      final String umlBlock = toUmlBlock(body, macroParams.getDiagramType(), umlSourceLocator);
      final List<String> config = new PlantUmlConfigBuilder().build(macroParams);
      final SourceStringReader reader = new SourceStringReader(new Defines(), umlBlock, config);
      try {
         reader.generateImage(resourceWriter.getStreamForWriting());
      } catch (IOException e) {
         throw new MacroException(e);
      }

      final StringBuilder sb = new StringBuilder();
      sb.append("<span class=\"image-wrap\" style=\"" + macroParams.getAlignment().getCssStyle() + "\">");

      sb.append("<img src='");
      sb.append(resourceWriter.getResourcePath());
      sb.append("'");
      sb.append(macroParams.getImageStyle());
      sb.append("/>");
      sb.append("</span>");
      final String result = sb.toString();
      return result.toString();
   }

   String toUmlBlock(final String body, final DiagramType diagramType, UmlSourceLocator umlSourceLocator)
         throws MacroException {

      final UmlSourceBuilder builder;
      try {
         builder = new UmlSourceBuilder(diagramType).append(new StringReader(body));
      } catch (IOException e) {
         throw new MacroException(e);
      }
      try {
         return new PlantUmlPreprocessor(builder.build(), umlSourceLocator).toUmlBlock();
      } catch (final IOException e) {
         throw new MacroException(e);
      }
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
