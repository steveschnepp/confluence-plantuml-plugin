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

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;

/**
 * This is the {flowchart} Macro (Confluence > 4.0).
 */
public class FlowChartMacroV4 implements Macro {

   private final PlantUmlMacroV4 plantUmlMacroV4;

   public FlowChartMacroV4(WritableDownloadResourceManager writeableDownloadResourceManager, PageManager pageManager,
         SpaceManager spaceManager, SettingsManager settingsManager, PluginAccessor pluginAccessor,
         ShortcutLinksManager shortcutLinksManager, PlantUmlConfigurationManager configurationManager) {
      plantUmlMacroV4 =
            new PlantUmlMacroV4(writeableDownloadResourceManager, pageManager, spaceManager, settingsManager,
                  pluginAccessor, shortcutLinksManager, configurationManager);
   }

   public String execute(Map<String, String> params, String body, ConversionContext context)
         throws MacroExecutionException {
      try {
         return new AbstractFlowChartMacroImpl() {
            @Override
            protected String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
                  throws MacroException {
               return plantUmlMacroV4.execute(params, dotString, context);
            }
         }.execute(params, body, context.getPageContext());
      } catch (MacroException e) {
         throw new MacroExecutionException(e);
      }
   }

   public BodyType getBodyType() {
      return plantUmlMacroV4.getBodyType();
   }

   public OutputType getOutputType() {
      return plantUmlMacroV4.getOutputType();
   }

}
