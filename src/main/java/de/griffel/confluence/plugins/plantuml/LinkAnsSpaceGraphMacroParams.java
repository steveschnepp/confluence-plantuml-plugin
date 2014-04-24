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

/**
 * Supported LinkGraph Macro parameters.
 */
public final class LinkAnsSpaceGraphMacroParams {
   public static final String TB = "TB";
   public static final String LR = "LR";

   private static final int DEFAULT_DEPTH = 3;
   private static final int DEFAULT_INCOMING_LINK_LEVELS = 1;
   private static final int DEFAULT_OUTGOING_LINK_LEVELS = 1;
   private static final int DEFAULT_NODE_FONTSIZE = 9;
   private static final String DEFAULT_NODE_COLOR = "lightyellow";
   private static final String DEFAULT_ROOT_NODE_COLOR = "lightblue";

   public enum Param {
      space,
      page,
      depth,              // only spacegraph
      incomingLinkLevels, // only link graph
      outgoingLinkLevels, // only link graph
      rootNodeColor,      // only link graph
      nodeColor,
      nodeFontsize,
      direction,
      metadata,
      debug;
   }

   private final Map<String, String> params;

   public LinkAnsSpaceGraphMacroParams(Map<String, String> params) {
      this.params = params;
   }


   public String getSpace() {
      return get(Param.space);
   }

   public String getPage() {
      return get(Param.page);
   }

   /**
    * Levels of childs to be processed.
    *
    * Only macro spacegraph
    * @return Number of levels to be processed.
    */
   public int getDepth() {
      try {
         return Integer.parseInt(get(Param.depth));
      } catch (NumberFormatException e) {
         return DEFAULT_DEPTH;
      }
   }

   /**
    * Levels of outgoing links to be processed.
    *
    * Only macro linkgraph
    * @return Number of levels to be processed.
    */
   public int getOutgoingLinkLevels() {
      try {
         return Integer.parseInt(get(Param.outgoingLinkLevels));
      } catch (NumberFormatException e) {
         return DEFAULT_OUTGOING_LINK_LEVELS;
      }
   }

   /**
    * Levels of incoming links to be processed.
    *
    * Only macro linkgraph
    * @return Number of levels to be processed.
    */
   public int getIncomingLinkLevels() {
      try {
         return Integer.parseInt(get(Param.incomingLinkLevels));
      } catch (NumberFormatException e) {
         return DEFAULT_INCOMING_LINK_LEVELS;
      }
   }

   /**
    * Color in which root node should be drawn.
    *
    * Only macro linkgraph
    * @return Color as name (e.g. red) or hex (e.g. #FF0000)
    */
   public String getRootNodeColor() {
      final String value = get(Param.rootNodeColor);
      return value != null ? value : DEFAULT_ROOT_NODE_COLOR;
   }

   public String getNodeColor() {
      final String value = get(Param.nodeColor);
      return value != null ? value : DEFAULT_NODE_COLOR;
   }

   public int getNodeFontsize() {
      try {
         return Integer.parseInt(get(Param.nodeFontsize));
      } catch (NumberFormatException e) {
         return DEFAULT_NODE_FONTSIZE;
      }
   }

   public String getDirection() {
      final String value = get(Param.direction);
      return TB.equals(value) ? value : LR;
   }

   public boolean isDirectionTopToBottom() {
      return TB.equals(getDirection());
   }

   public boolean isDirectionLeftToRight() {
      return LR.equals(getDirection());
   }

   public String getMetadata() {
      final String value = get(Param.metadata);
      return value != null ? value : "";
   }

   public boolean isDebug() {
      final String debug = get(Param.debug);
      return debug != null ? Boolean.valueOf(debug) : false;
   }

   @Override
   public String toString() {
      return "LinkGraphMacroParams [_params=" + params + "]";
   }

   private String get(Param param) {
      return params.get(param.name());
   }

}
