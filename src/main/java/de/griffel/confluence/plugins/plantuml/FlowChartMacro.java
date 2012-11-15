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

import net.sourceforge.plantuml.DiagramType;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;
import de.griffel.confluence.plugins.plantuml.type.GraphBuilder;

/**
 * This is the {flowchart} Macro.
 */
public class FlowChartMacro implements Macro {
   private final PlantUmlMacroV4 plantUmlMacro;

   public FlowChartMacro(WritableDownloadResourceManager writeableDownloadResourceManager, PageManager pageManager,
         SpaceManager spaceManager, SettingsManager settingsManager, PluginAccessor pluginAccessor,
         ShortcutLinksManager shortcutLinksManager, PlantUmlConfigurationManager configurationManager) {
      plantUmlMacro = new PlantUmlMacroV4(writeableDownloadResourceManager, pageManager, spaceManager, settingsManager,
            pluginAccessor, shortcutLinksManager, configurationManager);
   }

   public String execute(Map<String, String> params, String body, ConversionContext context)
         throws MacroExecutionException {

      final GraphBuilder graphBuilder = new GraphBuilder().appendGraph(body.trim());
      final String dotString = graphBuilder.build();

      params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
      params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.FALSE.toString());

      return plantUmlMacro.execute(params, dotString, context);
   }

   public BodyType getBodyType() {
      return plantUmlMacro.getBodyType();
   }

   public OutputType getOutputType() {
      return plantUmlMacro.getOutputType();
   }

}
