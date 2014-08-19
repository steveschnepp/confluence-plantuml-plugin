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

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 * This is the {svg} Macro (Confluence < 4.0).
 */
public class SvgMacro extends BaseMacro {

   private final SpaceManager _spaceManager;
   private final PageManager _pageManager;
   private final AttachmentManager _attachmentManager;

   public SvgMacro(PageManager pageManager, SpaceManager spaceManager, AttachmentManager attachmentManager, I18NBeanFactory i18NBeanFactory) {
      _spaceManager = spaceManager;
      _pageManager = pageManager;
      _attachmentManager = attachmentManager;
   }

   @SuppressWarnings("unchecked")
   public String execute(Map params, String body, RenderContext context) throws MacroException {
      return SvgMacro.getSvg(params, _spaceManager, _pageManager, _attachmentManager, context);
   }

   /*
    * (non-Javadoc)
    *
    * @see com.atlassian.renderer.v2.macro.Macro#getBodyRenderMode()
    */
   public RenderMode getBodyRenderMode() {
      return RenderMode.NO_RENDER;
   }

   public boolean hasBody() {
      return false;
   }

   public static String getSvg(Map<String, String> params, SpaceManager spaceManager, PageManager pageManager,
           AttachmentManager attachmentManager, RenderContext renderContext) throws MacroException {
      final SvgMacroParams macroParams = new SvgMacroParams(params);
      final String space = macroParams.getSpace();
      final String page = macroParams.getPage();
      final String attachment = macroParams.getAttachmentName();

      final PageContext pageContext = (PageContext) renderContext;
      final Space spaceObject;
      final Page pageObject;
      final Attachment attachmentObject;

      if (isSet(space)) {
         spaceObject = spaceManager.getSpace(trimToEmpty(space));
      } else {
         spaceObject = spaceManager.getSpace(pageContext.getSpaceKey());
      }
      if (spaceObject == null) {
         throw new MacroException("Invalid space: " + space);
      }

      if (isSet(page)) {
         pageObject = pageManager.getPage(spaceObject.getKey(), trimToEmpty(page));
      } else {
         pageObject = pageManager.getPage(spaceObject.getKey(), pageContext.getPageTitle());
      }
      if (pageObject == null) {
         throw new MacroException("Invalid page: " + spaceObject.getKey() + ":" + page);
      }

      if (isSet(attachment)) {
         attachmentObject = attachmentManager.getAttachment(pageObject, trimToEmpty(attachment));
      } else {
         throw new MacroException("Name of attachment required");
      }
      if (attachmentObject == null) {
         throw new MacroException("No attachment " + attachment + " found at " + spaceObject.getKey() + ":" + pageObject.getTitle());
      }

      final StringWriter sw = new StringWriter();
      try {
         IOUtils.copy(attachmentObject.getContentsAsStream(), sw);
      } catch (IOException ex) {
         throw new MacroException("Could not read attachment + " + attachment);
      }

      return sw.toString();
   }

   public static boolean isSet(String str) {
      return trimToEmpty(str).length() > 0;
   }

   public static String trimToEmpty(String str) {
      return str == null ? "" : str.trim();
   }
}
