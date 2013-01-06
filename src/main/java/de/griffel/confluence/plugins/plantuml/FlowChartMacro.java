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

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;

/**
 * This is the {flowchart} Macro (Confluence < 4.0).
 */
public class FlowChartMacro extends BaseMacro {
   protected final PlantUmlMacro plantUmlMacro;

   public FlowChartMacro(WritableDownloadResourceManager writeableDownloadResourceManager, PageManager pageManager,
         SpaceManager spaceManager, SettingsManager settingsManager, PluginAccessor pluginAccessor,
         ShortcutLinksManager shortcutLinksManager, ContextPathHolder contextPathHolder,
         PlantUmlConfigurationManager configurationManager) {
      plantUmlMacro = new PlantUmlMacro(writeableDownloadResourceManager, pageManager, spaceManager, settingsManager,
            pluginAccessor, shortcutLinksManager, contextPathHolder, configurationManager);
   }

   @SuppressWarnings("unchecked")
   public String execute(Map params, String body, RenderContext context) throws MacroException {
      return new AbstractFlowChartMacroImpl() {
         @Override
         protected String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
               throws MacroException {
            return plantUmlMacro.execute(params, dotString, context);
         }
      }.execute(params, body, context);
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.atlassian.renderer.v2.macro.Macro#getBodyRenderMode()
    */
   public RenderMode getBodyRenderMode() {
      return plantUmlMacro.getBodyRenderMode();
   }

   public boolean hasBody() {
      return plantUmlMacro.hasBody();
   }

}
