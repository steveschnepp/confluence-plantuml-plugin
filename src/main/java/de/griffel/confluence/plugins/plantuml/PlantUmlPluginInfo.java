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

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;

/**
 * PlantUML plugin info.
 */
public final class PlantUmlPluginInfo {
   public static final String PLUGIN_KEY = "de.griffel.confluence.plugins.plant-uml";

   static final String PLANTUML_VERSION_INFO_REGEX = "(?s)(?:.*)@startuml\\s*(version|about|testdot)\\s*@enduml(?:.*)";

   private final PluginAccessor pluginAccessor;

   public PlantUmlPluginInfo(PluginAccessor pluginAccessor) {
      this.pluginAccessor = pluginAccessor;
   }

   @Override
   public String toString() {
      final PluginInformation info = getPluginInformation();
      final StringBuilder sb = new StringBuilder();
      sb.append(info.getDescription());
      sb.append(", Version: ");
      sb.append(info.getVersion());
      sb.append(", URL=");
      sb.append(info.getVendorUrl());
      sb.append(" by ");
      sb.append(info.getVendorName());
      return sb.toString();
   }

   public String toHtmlString() {
      final PluginInformation info = getPluginInformation();
      final StringBuilder sb = new StringBuilder();
      sb.append("<div style=\"margin: 20px 0 15px 0;\">");
      sb.append(info.getDescription());
      sb.append(" Version: <b>");
      sb.append(info.getVersion());
      sb.append("</b> by ");
      sb.append(info.getVendorName());
      sb.append(". ");
      sb.append("<a href=\"");
      sb.append(info.getVendorUrl());
      sb.append("\">Plugin Homepage</a>");
      sb.append("</div>");
      return sb.toString();
   }

   public PluginInformation getPluginInformation() {
      return getPlugin().getPluginInformation();
   }

   public Plugin getPlugin() {
      return pluginAccessor.getPlugin(PLUGIN_KEY);
   }
}
