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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.core.DiagramType;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

/**
 * This is the abstract implementation class of the {spacegraph} macro.
 */
abstract class AbstractSpaceGraphMacroImpl {
   protected PermissionManager _pm;
   protected String _baseUrl;
   
   public String execute(Map<String, String> params, String dotString, RenderContext context) throws MacroException {
      final SpaceGraphMacroParams macroParams = new SpaceGraphMacroParams(params);          
      
      params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
      if (macroParams.isDebug()) {
         params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.TRUE.toString());
      }

      return executePlantUmlMacro(params, dotString, context);
   }

   
   protected abstract String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
         throws MacroException;

   
   /**
    * Create dot-String {spacegraph}  
    */
   public String createDotForSpaceGraph(Map<String, String> params, PageContext pageContext, SpaceManager spaceManager,
         PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager) {
      
      _baseUrl = settingsManager.getGlobalSettings().getBaseUrl();      
      _pm = permissionManager;
      
      final SpaceGraphMacroParams macroParams = new SpaceGraphMacroParams(params);
            
      String spaceKey = macroParams.getSpace();
      if (spaceKey == null) {
         spaceKey = pageContext.getSpaceKey();
      }      
      
      final StringBuilder sb = new StringBuilder("digraph g {\n");
      sb.append("edge [arrowsize=\"0.8\"];");
      sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"lightyellow\", fontname=\"Verdana\", fontsize=\"");
      sb.append(macroParams.getNodeFontsize()).append("\"];\n");      
      sb.append("rankdir=");
      if ("TB".equals(macroParams.getDirection())) {
         sb.append("TB\n"); 
      } else {
         sb.append("LR\n");
      }

      final List<Page> rootPages = new ArrayList<Page>();

      String startPageTitle = macroParams.getPage();
      if (startPageTitle != null) {
         // Seitenname kann sein: @self, <spacekey>:<title> oder <title>
         if ("@self".equals(startPageTitle)) {
            startPageTitle = pageContext.getPageTitle();
            spaceKey = pageContext.getSpaceKey();
         } else if (startPageTitle.matches(".*:.*")) {
            String[] spaceAndTitle = startPageTitle.split(":", 2);
            spaceKey = spaceAndTitle[0];
            startPageTitle = spaceAndTitle[1];
         }
         final Page startPage = pageManager.getPage(spaceKey, startPageTitle);
         if ((startPage != null) &&  isViewPermitted(startPage)) {
            rootPages.add(startPage);
            sb.append(buildDotNode(startPage.getDisplayTitle(), _baseUrl + startPage.getUrlPath()));
         } else {
            sb.append(buildDotNode(spaceKey + "/" + startPageTitle + " not found"));
         }
      } else {      
         final Space space = spaceManager.getSpace(spaceKey);
         final List<Page> pageList = pageManager.getPages(space, true /* only current ones */);
         for (Page page : pageList) {            
            if (page.getAncestors().isEmpty() && isViewPermitted(page)) {
               rootPages.add(page);
               sb.append(buildDotNode(page.getDisplayTitle(), _baseUrl + page.getUrlPath()));
            }
         }
      }

      sb.append(processChildren(rootPages, macroParams.getDepth(), 0 /* currentDepth */));
      sb.append("}\n");
      
      return sb.toString();
   }

   /**
    * Creates parent-child-edges
    * @param parents
    * @return
    */
   String processChildren(List<Page> parents, int depth, int currentDepth) {
      if (currentDepth >= depth) {
         return "";
      }
      
      final StringBuilder sb = new StringBuilder();
      
      for (Page page : parents) {
         final List<Page> children = page.getChildren();
         
         sb.append(processChildren(children, depth, currentDepth+1));
         for (Page child : children) {
            if (isViewPermitted(child)) {
               sb.append(buildDotNode(child.getDisplayTitle(), _baseUrl + child.getUrlPath()));
               sb.append(buildDotEdge(page.getDisplayTitle(), child.getDisplayTitle()));
            }
         }
      }      
      return sb.toString();
   }

   
   public String buildDotEdge(String left, String right) {
      // "left" -> "right";
      return new StringBuilder("\"").append(left).append("\" -> \"").append(right).append("\";\n").toString();
   }
   
   
   public String buildDotNode(String node, String url) {
      // "node" [URL="url"];
      return new StringBuilder("\"").append(node).append("\" [URL=\"").append(url).append("\"];\n").toString(); 
   }
   
   
   public String buildDotNode(String node) {
      // "node";
      return new StringBuilder("\"").append(node).append("\";\n").toString(); 
   }

   
   public boolean isViewPermitted(Page page) {
      return _pm.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, page);
   }

}
