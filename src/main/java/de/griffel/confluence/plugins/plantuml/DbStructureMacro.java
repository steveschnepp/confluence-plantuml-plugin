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

import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;

/**
 * This is the {dbstructure} Macro (Confluence < 4.0).
 */
public class DbStructureMacro extends BaseMacro {
   protected final PlantUmlMacro plantUmlMacro;
   private final SpaceManager _spaceManager;
   private final PageManager _pageManager;
   private final SettingsManager _settingsManager;
   private final PermissionManager _permissionManager;
   private final ContentPropertyManager _contentPropertyManager;
   
   public DbStructureMacro(WritableDownloadResourceManager writeableDownloadResourceManager, PageManager pageManager,
         SpaceManager spaceManager, SettingsManager settingsManager, PluginAccessor pluginAccessor,
         ShortcutLinksManager shortcutLinksManager, PlantUmlConfigurationManager configurationManager,
         PermissionManager permissionManager, ContentPropertyManager contentPropertyManager, I18NBeanFactory i18NBeanFactory) {

      _spaceManager = spaceManager;
      _pageManager = pageManager;
      _settingsManager = settingsManager;
      _permissionManager = permissionManager;
      _contentPropertyManager = contentPropertyManager;

      plantUmlMacro = new PlantUmlMacro(writeableDownloadResourceManager, pageManager, spaceManager, settingsManager,
            pluginAccessor, shortcutLinksManager, configurationManager, i18NBeanFactory);
   }

   @SuppressWarnings("unchecked")
   public String execute(Map params, String body, RenderContext context) throws MacroException {
      return new AbstractDbStructureMacroImpl() {
         @Override
         protected String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
               throws MacroException {
            String realString = createDotForDbStructure(params, (PageContext) context,
                  _spaceManager, _pageManager, _settingsManager, _permissionManager, _contentPropertyManager);
            return plantUmlMacro.execute(params, realString, context);
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
      return false;
   }

}
