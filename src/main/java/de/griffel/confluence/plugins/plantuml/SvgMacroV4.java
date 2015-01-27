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

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

/**
 * This is the {spacegraph} Macro (Confluence > 4.0).
 */
public class SvgMacroV4 implements Macro {

   private final SpaceManager _spaceManager;
   private final PageManager _pageManager;
   private final AttachmentManager _attachmentManager;

   public SvgMacroV4(PageManager pageManager, SpaceManager spaceManager, AttachmentManager attachmentManager) {
      _spaceManager = spaceManager;
      _pageManager = pageManager;
      _attachmentManager = attachmentManager;
   }

   public String execute(Map<String, String> params, String body, ConversionContext context)
           throws MacroExecutionException {
      try {
         return SvgMacro.getSvg(params, _spaceManager, _pageManager, _attachmentManager, context.getPageContext());
      } catch (MacroException e) {
         throw new MacroExecutionException(e);
      }
   }

   public BodyType getBodyType() {
      return BodyType.NONE;
   }

   public OutputType getOutputType() {
      return OutputType.INLINE;
   }

}
