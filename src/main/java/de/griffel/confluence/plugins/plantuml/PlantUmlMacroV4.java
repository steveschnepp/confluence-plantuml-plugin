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
import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;

/**
 * PlantUmlMacro for Confluence V 4.x.
 */
public class PlantUmlMacroV4 extends PlantUmlMacro implements Macro {

   public PlantUmlMacroV4(WritableDownloadResourceManager writeableDownloadResourceManager, PageManager pageManager,
         SpaceManager spaceManager, SettingsManager settingsManager, PluginAccessor pluginAccessor,
         ShortcutLinksManager shortcutLinksManager, PlantUmlConfigurationManager configurationManager) {
      super(writeableDownloadResourceManager, pageManager, spaceManager, settingsManager, pluginAccessor,
            shortcutLinksManager, configurationManager);
   }

   public final BodyType getBodyType() {
      return BodyType.PLAIN_TEXT;
   }

   public final OutputType getOutputType() {
      return OutputType.BLOCK;
   }

   public String execute(Map<String, String> params, String body, ConversionContext context)
         throws MacroExecutionException {
      try {
         return executeInternal(params, body, context.getPageContext());
      } catch (final IOException e) {
         throw new MacroExecutionException(e);
      } catch (MacroException e) {
         throw new MacroExecutionException(e);
      } catch (UnauthorizedDownloadResourceException e) {
         throw new MacroExecutionException(e);
      } catch (DownloadResourceNotFoundException e) {
         throw new MacroExecutionException(e);
      }
   }

}
