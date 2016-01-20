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

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLink;
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
import net.sourceforge.plantuml.core.DiagramType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {linkgraph} and {spacegraph} macros.
 */
abstract class AbstractLinkAndSpaceGraphMacroImpl {

   private ContentPropertyManager _cpm;
   private PermissionManager _pm;
   private String _baseUrl;
   private LinkAndSpaceGraphMacroParams _macroParams;

   public String execute(Map<String, String> params, String dotString, RenderContext context) throws MacroException {
      final LinkAndSpaceGraphMacroParams macroParams = new LinkAndSpaceGraphMacroParams(params);

      params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
      if (macroParams.isDebug()) {
         params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.TRUE.toString());
      }

      return executePlantUmlMacro(params, dotString, context);
   }

   protected abstract String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
           throws MacroException;

   // ============================= SPACE GRAPH =============================
   public String createDotForSpaceGraph(Map<String, String> params, PageContext pageContext, SpaceManager spaceManager,
           PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager,
           ContentPropertyManager contentPropertyManager) {

      _baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      _pm = permissionManager;
      _cpm = contentPropertyManager;
      _macroParams = new LinkAndSpaceGraphMacroParams(params);

      String spaceKey = _macroParams.getSpace();
      if (spaceKey == null) {
         spaceKey = pageContext.getSpaceKey();
      }

      final StringBuilder sb = new StringBuilder("digraph g {\n");
      sb.append("edge [arrowsize=\"0.8\"];");
      sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"").append(_macroParams.getNodeColor()).append("\",");
      sb.append("fontname=\"Verdana\", fontsize=\"").append(_macroParams.getNodeFontsize()).append("\"];\n");
      sb.append("rankdir=").append(_macroParams.getDirection()).append("\n");

      final List<Page> rootPages = new ArrayList<Page>();

      String startPageTitle = _macroParams.getPage();
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
         if ((startPage != null)
               && isViewPermitted(startPage)
               && !isTrashed(startPage)) {
            rootPages.add(startPage);
            sb.append(buildDotNode(startPage));
         } else {
            sb.append(buildDotNode(spaceKey + "/" + startPageTitle + " not found"));
         }
      } else {
         final Space space = spaceManager.getSpace(spaceKey);

         final List<Page> pageList = pageManager.getPages(space, true /* only current ones */);
         for (Page page : pageList) {
            if (page.getAncestors().isEmpty()
                  && isViewPermitted(page)
                  && !isTrashed(page)) {
               rootPages.add(page);
               sb.append(buildDotNode(page));
            }
         }
      }

      sb.append(processChildren(rootPages, _macroParams.getDepth(), 0 /* currentDepth */));
      sb.append("}\n");

      return sb.toString();
   }

   String processChildren(List<Page> parents, int depth, int currentDepth) {
      if (currentDepth >= depth) {
         return "";
      }

      final StringBuilder sb = new StringBuilder();

      for (Page page : parents) {
         final List<Page> children = page.getChildren();

         sb.append(processChildren(children, depth, currentDepth + 1));
         for (Page child : children) {
            if (isViewPermitted(child)
                  && !isTrashed(child)) {
               sb.append(buildDotNode(child));
               sb.append(buildDotEdge(quote(page.getDisplayTitle()), quote(child.getDisplayTitle())));
            }
         }
      }
      return sb.toString();
   }

   // ============================= LINK GRAPH =============================
   /**
    * Create dot-String {linkgraph}
    */
   public String createDotForLinkGraph(Map<String, String> params, PageContext pageContext, SpaceManager spaceManager,
           PageManager pageManager, SettingsManager settingsManager, PermissionManager permissionManager,
           ContentPropertyManager contentPropertyManager, LinkManager linkManager) {

      _baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      _pm = permissionManager;
      _cpm = contentPropertyManager;
      _macroParams = new LinkAndSpaceGraphMacroParams(params);

      String spaceKey = _macroParams.getSpace();
      if (spaceKey == null) {
         spaceKey = pageContext.getSpaceKey();
      }

      final StringBuilder sb = new StringBuilder("digraph g {\n");
      sb.append("edge [arrowsize=\"0.8\"];");
      sb.append("node [shape=\"rect\", style=\"filled\", fillcolor=\"").append(_macroParams.getNodeColor()).append("\",");
      sb.append("fontname=\"Verdana\", fontsize=\"").append(_macroParams.getNodeFontsize()).append("\"];\n");
      sb.append("rankdir=").append(_macroParams.getDirection()).append("\n");

      String startPageTitle = _macroParams.getPage();

      // Seitenname kann sein: @self, <spacekey>:<title> oder <title>
      if (startPageTitle == null || "@self".equals(startPageTitle)) {
         startPageTitle = pageContext.getPageTitle();
         spaceKey = pageContext.getSpaceKey();
      } else if (startPageTitle.matches(".*:.*")) {
         String[] spaceAndTitle = startPageTitle.split(":", 2);
         spaceKey = spaceAndTitle[0];
         startPageTitle = spaceAndTitle[1];
      }

      final Collection<ContentEntityObject> rootPages = new ArrayList<ContentEntityObject>();
      final Page startPage = pageManager.getPage(spaceKey, startPageTitle);
      if (startPage != null
            && isViewPermitted(startPage)
            && !isTrashed(startPage)) {

         rootPages.add(startPage);

         final int currentDepth = 0;
         processReferredPages(sb, rootPages, _macroParams.getOutgoingLinkLevels(), currentDepth, pageManager);
         processReferringPages(sb, rootPages, _macroParams.getIncomingLinkLevels(), currentDepth, pageManager, linkManager);

         // process root node with different color as last to prevent overwriting
         sb.append(buildDotNodeWithColor(startPage, _macroParams.getRootNodeColor()));
      } else {
         sb.append(buildDotNode(spaceKey + "/" + startPageTitle + " not found"));
      }

      sb.append("}\n");

      return sb.toString();
   }

   void processReferringPages(StringBuilder sb, Collection<ContentEntityObject> pagesToFindReferringOnes, int maxDepth, int currentDepth, PageManager pageManager, LinkManager linkManager) {
      if (currentDepth >= maxDepth) {
         return;
      }

      for (ContentEntityObject currentPage : pagesToFindReferringOnes) {
         final Collection<ContentEntityObject> visibleReferringPages = new ArrayList<ContentEntityObject>();

         for (ContentEntityObject referringPage : linkManager.getReferringContent(currentPage)) {
            if ((referringPage != null)
                  && (currentPage.getId() != referringPage.getId())
                  && isViewPermitted(referringPage)
                  && !isTrashed(referringPage)) {
               visibleReferringPages.add(referringPage);
            }
         }
         processReferringPages(sb, visibleReferringPages, maxDepth, currentDepth + 1, pageManager, linkManager);

         for (ContentEntityObject referringPage : visibleReferringPages) {
            sb.append(buildDotNode(referringPage));
            sb.append(buildDotEdge(quote(referringPage.getDisplayTitle()), quote(currentPage.getDisplayTitle())));
         }

      }
   }

   void processReferredPages(StringBuilder sb, Collection<ContentEntityObject> pagesToFindOutgoingLinks, int maxDepth, int currentDepth, PageManager pageManager) {
      if (currentDepth >= maxDepth) {
         return;
      }

      for (ContentEntityObject currentPage : pagesToFindOutgoingLinks) {
         final Collection<ContentEntityObject> visibleReferredPages = new ArrayList<ContentEntityObject>();

         for (OutgoingLink outgoingLink : currentPage.getOutgoingLinks()) {
            if (!outgoingLink.isUrlLink()) {
               final Page referredPage = pageManager.getPage(outgoingLink.getDestinationSpaceKey(), outgoingLink.getDestinationPageTitle());
               if ((referredPage != null)
                     && (referredPage.getId() != currentPage.getId())
                     && isViewPermitted(referredPage)
                     && !isTrashed(referredPage)) {
                  visibleReferredPages.add(referredPage);
               }
            }
         }
         processReferredPages(sb, visibleReferredPages, maxDepth, currentDepth + 1, pageManager);
         for (ContentEntityObject referredPage : visibleReferredPages) {
            sb.append(buildDotNode(referredPage));
            sb.append(buildDotEdge(quote(currentPage.getDisplayTitle()), quote(referredPage.getDisplayTitle())));
         }
      }
   }

   // ============================= COMMON FUNCTIONS =============================
   public String buildDotEdge(String left, String right) {
      // "left" -> "right";
      return "\"" + left + "\" -> \"" + right + "\";\n";
   }

   public String buildDotNode(ContentEntityObject page) {
      return buildDotNodeWithColor(page, _macroParams.getNodeColor());
   }

   public String buildDotNodeWithColor(ContentEntityObject page, String color) {
      /* "TOP-DOWN" [ label = "{TD | {{Key 1|Key 555}|{Value 1|Value 2}} }"
         shape="record"
         URL="url"];

         "LEFT-RIGHT" [ label = "LR | {{Key 1|Key2} | {Value 1|Value 2}}"
         shape="record"
         URL="url"];  */
      final String metadataString = buildMetadataString(page);
      StringBuilder sb = new StringBuilder("\"");
      sb.append(quote(page.getDisplayTitle())).append("\" [ label = \"");
      if (metadataString.length() > 0 && _macroParams.isDirectionTopToBottom()) {
         sb.append("{");
      }
      sb.append(quote(page.getDisplayTitle())).append(buildMetadataString(page));
      if (metadataString.length() > 0 && _macroParams.isDirectionTopToBottom()) {
         sb.append("}");
      }
      sb.append("\"\nshape=\"record\"\n")
              .append("fillcolor=\"").append(color).append("\"\n")
              .append("URL=\"").append(_baseUrl).append(page.getUrlPath()).append("\"];\n");
      return sb.toString();
   }

   public String buildDotNode(String node) {
      // "node";
      return "\"" + node + "\";\n";
   }

   public boolean isViewPermitted(ContentEntityObject page) {
      return _pm.hasPermission(AuthenticatedUserThreadLocal.get(), Permission.VIEW, page);
   }

   protected boolean isTrashed(ContentEntityObject page) {
      return page.getContentStatusObject() == ContentStatus.TRASHED;
   }

   private String buildMetadataString(ContentEntityObject page) {
      if (StringUtils.isEmpty(_macroParams.getMetadata())) {
         return "";
      }

      final Map<String, String> metadata = filterMetadataValues(getMetadataValues(page), getMetadataKeysToShow());
      if (metadata.isEmpty()) {
         return "";
      }

      // {{Key 1|Key 2|Key 3} | {Value 1|Value 2|Value 3}}"
      final String[] keys = new String[metadata.size()];
      final String[] values = new String[metadata.size()];
      int i = 0;
      for (Map.Entry<String, String> entry : metadata.entrySet()) {
         keys[i] = entry.getKey();
         values[i++] = quote(entry.getValue());
      }

      return " | {{" +
            StringUtils.join(keys, "|") +
            "} | {" +
            StringUtils.join(values, "|") +
            "}}";
   }

   /**
    * Quotes special characters in metadata values.
    *
    * @see <a href="http://www.graphviz.org/doc/info/shapes.html#record">GraphViz Record</a>
    * @param s String to clean
    * @return Cleaned string
    */
   private String quote(String s) {
      return s.replaceAll("([<>{}|\"])", "\\\\$1");
   }

   private Set<String> getMetadataKeysToShow() {
      final Set<String> result = new HashSet<String>();
      for (String metadataKey : _macroParams.getMetadata().split(",")) {
         result.add(metadataKey.trim());
      }
      return result;
   }

   /**
    * Filters map.
    *
    * @param map meta data map
    * @param allowedKeys Set of keys that are allowed in map. "@all" allows all keys.
    * @return New map that contains only allowed keys.
    */
   private Map<String, String> filterMetadataValues(Map<String, String> map, Set<String> allowedKeys) {
      final Map<String, String> result = new HashMap<String, String>();
      for (Map.Entry<String, String> entry : map.entrySet()) {
         if (allowedKeys.contains("@all") || allowedKeys.contains(entry.getKey())) {
            result.put(entry.getKey(), entry.getValue());
         }
      }
      return result;
   }

   /**
    * Retrieves Metadata of a given page.
    *
    * @param page Page to get metadata keys for
    * @return Map with metadata.
    */
   private Map<String, String> getMetadataValues(ContentEntityObject page) {
      final String metadataprefix = "metadata.";
      final Map<String, String> map = new HashMap<String, String>();

      for (String k : getMetadataKeysForPage(page)) {
         final String value = _cpm.getTextProperty(page, metadataprefix + k);
         if (value != null && value.length() > 0) {
            map.put(k, value);
         }
      }
      return map;
   }

   /**
    * Get all Metadata keys of a page.
    *
    * The Metadata Plugin stores all its data in OS_PROPERTYENTRY. They are accessible via the ContentPropertyManager.
    * As it doesn't have a method to retrieve all properties of a given page, the list of metadata keys is stored in a
    * property called "metadatakeys". The keys itself are separated by |
    *
    * @param page Page to get metadata keys for
    *
    * @return Array with Metadata keys
    */
   private String[] getMetadataKeysForPage(ContentEntityObject page) {
      final String metadatakeys = "metadatakeys";
      final String keys = _cpm.getTextProperty(page, metadatakeys);
      if (StringUtils.isEmpty(keys)) {
         return new String[0];
      }
      return keys.split("\\|");
   }

}
