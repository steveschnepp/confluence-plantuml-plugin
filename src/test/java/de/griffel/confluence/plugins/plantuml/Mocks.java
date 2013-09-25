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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Map;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
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

   private final PluginAccessor pluginAccessor = mock(PluginAccessor.class);
   private final Plugin plugin = mock(Plugin.class);
   private final PluginInformation pluginInfo = mock(PluginInformation.class);
   private final SpaceManager spaceManager = mock(SpaceManager.class);
   private final Space plantUmlSpaceMock = mock(Space.class);
   private final PreprocessingContext preprocessingContext = mock(PreprocessingContext.class);
   private final ShortcutLinksManager shortcutLinksManager = mock(ShortcutLinksManager.class);
   private final PlantUmlConfigurationManager configurationManager = mock(PlantUmlConfigurationManager.class);

   public Mocks() {
      when(pluginAccessor.getPlugin(PlantUmlPluginInfo.PLUGIN_KEY)).thenReturn(plugin);
      when(plugin.getPluginInformation()).thenReturn(pluginInfo);
      when(pluginInfo.getVersion()).thenReturn("1.x");
      when(pluginInfo.getVendorName()).thenReturn("Vendor");
      when(pluginInfo.getVendorUrl()).thenReturn("URL");
      when(pluginInfo.getDescription()).thenReturn("blabla");

      when(spaceManager.getSpace(new PageContextMock().getSpaceKey())).thenReturn(plantUmlSpaceMock);
      when(plantUmlSpaceMock.getName()).thenReturn("PlantUML Space");

      final ShortcutLinkConfig googleShortcutLinkConfig = new ShortcutLinkConfig();
      googleShortcutLinkConfig.setDefaultAlias("Google Search with '%s'");
      googleShortcutLinkConfig.setExpandedValue("http://www.google.com/search?q=%s");
      when(shortcutLinksManager.getShortcutLinks()).thenReturn(
            ImmutableMap.of("google", googleShortcutLinkConfig));
      final Map<String, ShortcutLinkConfig> shortcutLinks = shortcutLinksManager.getShortcutLinks();

      when(preprocessingContext.getBaseUrl()).thenReturn(BASE_URL);
      when(preprocessingContext.getPageContext()).thenReturn(new PageContextMock());
      when(preprocessingContext.getSpaceManager()).thenReturn(spaceManager);
      when(preprocessingContext.getShortcutLinks()).thenReturn(shortcutLinks);

      when(configurationManager.load()).thenReturn(new PlantUmlConfigurationBean());
   }

   public String getBaseUrl() {
      return BASE_URL;
   }

   public PreprocessingContext getPreprocessingContext() {
      return preprocessingContext;
   }

   public PluginAccessor getPluginAccessor() {
      return pluginAccessor;
   }

   public SpaceManager getSpaceManager() {
      return spaceManager;
   }

   public SettingsManager getSettingsManager() {
      return new MockSettingsManager();
   }

   public ShortcutLinksManager getShortcutLinksManager() {
      return shortcutLinksManager;
   }

   public PlantUmlConfigurationManager getConfigurationManager() {
      return configurationManager;
   }

   public I18NBeanFactory getI18NBeanFactory() {
      I18NBeanFactory mock = mock(I18NBeanFactory.class);
      I18NBean i18NBeanMock = mock(I18NBean.class);
      when(i18NBeanMock.getText(Matchers.anyString())).thenAnswer(new Answer<String>() {
         public String answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            return "__" + args[0] + "__";
         }
      });
      when(mock.getI18NBean()).thenReturn(i18NBeanMock);
      return mock;
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
