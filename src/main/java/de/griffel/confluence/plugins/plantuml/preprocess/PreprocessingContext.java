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

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.spaces.SpaceManager;

/**
 * Preprocessing Context.
 */
public interface PreprocessingContext {

   /**
    * Returns the base URL of a Confluence installation.
    * 
    * @return the base URL of a Confluence installation.
    */
   String getBaseUrl();

   /**
    * Returns the current Confluence page context.
    * 
    * @return the current Confluence page context.
    */
   PageContext getPageContext();

   /**
    * Returns a reference to a space manager.
    * 
    * @return a reference to a space manager.
    */
   SpaceManager getSpaceManager();

   /**
    * Returns a reference to a page manager.
    * 
    * @return a reference to a page manager.
    */
   PageManager getPageManager();

   /**
    * Returns the shortcut link map.
    * 
    * @return the shortcut link map.
    */
   Map<String, ShortcutLinkConfig> getShortcutLinks();

   /**
    * Returns the page anchor builder for this confluence version.
    * 
    * @return the page anchor builder.
    */
   PageAnchorBuilder getPageAnchorBuilder();
}
