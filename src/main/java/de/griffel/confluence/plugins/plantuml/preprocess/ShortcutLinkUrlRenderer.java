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
package de.griffel.confluence.plugins.plantuml.preprocess;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.base.Preconditions;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Render Shortcut Links like 'foo@google'.
 */
public class ShortcutLinkUrlRenderer extends AbstractUrlRenderer {
   /**
    * Separator string for shortcut links.
    */
   public static final String SEPARATOR = "@";

   private static final String PLACEHOLDER = "%s";

   private final Map<String, ShortcutLinkConfig> _shortcutLinkMap;

   public ShortcutLinkUrlRenderer(Map<String, ShortcutLinkConfig> shortcutLinkMap) {
      _shortcutLinkMap = shortcutLinkMap;
   }

   /**
    * {@inheritDoc}
    */
   public String getHyperlink(ConfluenceLink link) throws MacroException {
      Preconditions.checkArgument(link.getPageTitle().contains(SEPARATOR));

      final String pageTitle = link.getPageTitle();
      final String shortcutValue = getShortcutValue(pageTitle);
      final ShortcutLinkConfig shortcutLinkConfig = getShortcutLinkConfig(pageTitle, shortcutValue);
      final String expandedValue = shortcutLinkConfig.getExpandedValue();

      final String result = substitude(expandedValue, shortcutValue);
      return result;
   }

   @Override
   public String getDefaultAlias(PreprocessingContext context, ConfluenceLink link) throws MacroException {
      final String pageTitle = link.getPageTitle();
      final String shortcutValue = getShortcutValue(pageTitle);
      final ShortcutLinkConfig shortcutLinkConfig = getShortcutLinkConfig(pageTitle, shortcutValue);

      final String result;
      if (StringUtils.isEmpty(shortcutLinkConfig.getDefaultAlias())) {
         result = super.getDefaultAlias(context, link);
      } else {
         result = substitude(shortcutLinkConfig.getDefaultAlias(), shortcutValue);
      }

      return result;
   }

   /**
    * @param pageTitle
    * @return
    */
   private String getShortcutValue(final String pageTitle) {
      return StringUtils.substringBefore(pageTitle, SEPARATOR);
   }

   /**
    * @param pageTitle
    * @param shortcutValue
    * @param shortcutKey
    * @return
    */
   private ShortcutLinkConfig getShortcutLinkConfig(final String pageTitle, final String shortcutValue) {
      final String shortcutKey = StringUtils.substringAfter(pageTitle, SEPARATOR);

      if (StringUtils.isEmpty(shortcutValue)) {
         throw new IllegalArgumentException("Invalid shortcut link. Missing value for link '" + pageTitle + "'.");
      }
      if (StringUtils.isEmpty(shortcutKey)) {
         throw new IllegalArgumentException("Invalid shortcut link. Missing key for link '" + pageTitle + "'.");
      }

      if (!_shortcutLinkMap.containsKey(shortcutKey)) {
         throw new IllegalArgumentException("Unknown shortcut key '" + shortcutKey + "'.");
      }

      final ShortcutLinkConfig shortcutLinkConfig = _shortcutLinkMap.get(shortcutKey);
      return shortcutLinkConfig;
   }

   static String substitude(final String source, final String value) {
      final String result;
      if (source.contains(PLACEHOLDER)) {
         result = source.replace(PLACEHOLDER, value);
      } else {
         result = source + value;
      }
      return result;
   }

}
