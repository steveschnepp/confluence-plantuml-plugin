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

import java.io.Serializable;
import java.util.Map;

import org.mockito.Mockito;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.google.common.collect.ImmutableMap;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationBean;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;
import de.griffel.confluence.plugins.plantuml.preprocess.PageContextMock;
import de.griffel.confluence.plugins.plantuml.preprocess.PreprocessingContext;

/**
 * Holds the Mock objects for unit testing.
 */
public final class Mocks {

   private static final String BASE_URL = "http://localhost:8080/confluence";

   private final PluginAccessor _pluginAccessor = Mockito.mock(PluginAccessor.class);
   private final Plugin _plugin = Mockito.mock(Plugin.class);
   private final PluginInformation _pluginInfo = Mockito.mock(PluginInformation.class);
   private final SpaceManager _spaceManager = Mockito.mock(SpaceManager.class);
   private final Space _plantUmlSpaceMock = Mockito.mock(Space.class);
   private final PreprocessingContext _preprocessingContext = Mockito.mock(PreprocessingContext.class);
   private final ShortcutLinksManager _shortcutLinksManager = Mockito.mock(ShortcutLinksManager.class);
   private final PlantUmlConfigurationManager _configurationManager = Mockito.mock(PlantUmlConfigurationManager.class);

   public Mocks() {
      Mockito.when(_pluginAccessor.getPlugin(PlantUmlPluginInfo.PLUGIN_KEY)).thenReturn(_plugin);
      Mockito.when(_plugin.getPluginInformation()).thenReturn(_pluginInfo);
      Mockito.when(_pluginInfo.getVersion()).thenReturn("1.x");
      Mockito.when(_pluginInfo.getVendorName()).thenReturn("Vendor");
      Mockito.when(_pluginInfo.getVendorUrl()).thenReturn("URL");
      Mockito.when(_pluginInfo.getDescription()).thenReturn("blabla");

      Mockito.when(_spaceManager.getSpace(new PageContextMock().getSpaceKey())).thenReturn(_plantUmlSpaceMock);
      Mockito.when(_plantUmlSpaceMock.getName()).thenReturn("PlantUML Space");

      final ShortcutLinkConfig googleShortcutLinkConfig = new ShortcutLinkConfig();
      googleShortcutLinkConfig.setDefaultAlias("Google Search with '%s'");
      googleShortcutLinkConfig.setExpandedValue("http://www.google.com/search?q=%s");
      Mockito.when(_shortcutLinksManager.getShortcutLinks()).thenReturn(
            ImmutableMap.of("google", googleShortcutLinkConfig));
      final Map<String, ShortcutLinkConfig> shortcutLinks = _shortcutLinksManager.getShortcutLinks();

      Mockito.when(_preprocessingContext.getBaseUrl()).thenReturn(BASE_URL);
      Mockito.when(_preprocessingContext.getPageContext()).thenReturn(new PageContextMock());
      Mockito.when(_preprocessingContext.getSpaceManager()).thenReturn(_spaceManager);
      Mockito.when(_preprocessingContext.getShortcutLinks()).thenReturn(shortcutLinks);

      Mockito.when(_configurationManager.load()).thenReturn(new PlantUmlConfigurationBean());

   }

   public String getBaseUrl() {
      return BASE_URL;
   }

   public PreprocessingContext getPreprocessingContext() {
      return _preprocessingContext;
   }

   public PluginAccessor getPluginAccessor() {
      return _pluginAccessor;
   }

   public SpaceManager getSpaceManager() {
      return _spaceManager;
   }

   public SettingsManager getSettingsManager() {
      return new MockSettingsManager();
   }

   public ShortcutLinksManager getShortcutLinksManager() {
      return _shortcutLinksManager;
   }

   public PlantUmlConfigurationManager getConfigurationManager() {
      return _configurationManager;
   }

   private static class MockSettingsManager implements SettingsManager {

      public GlobalDescription getGlobalDescription() {
         throw new UnsupportedOperationException();
      }

      public Settings getGlobalSettings() {
         return new Settings() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getBaseUrl() {
               return "http://localhost:8080/confluence";
            }
         };
      }

      public Serializable getPluginSettings(String arg0) {
         throw new UnsupportedOperationException();
      }

      public SpaceSettings getSpaceSettings(String arg0) {
         throw new UnsupportedOperationException();
      }

      public void updateGlobalDescription(GlobalDescription arg0) {
         throw new UnsupportedOperationException();
      }

      public void updateGlobalSettings(Settings arg0) {
         throw new UnsupportedOperationException();
      }

      public void updatePluginSettings(String arg0, Serializable arg1) {
         throw new UnsupportedOperationException();
      }

      public void updateSpaceSettings(SpaceSettings arg0) {
         throw new UnsupportedOperationException();
      }

   }

}
