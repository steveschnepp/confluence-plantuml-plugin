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

import java.math.BigDecimal;
import java.util.Map;

import de.griffel.confluence.plugins.plantuml.type.GraphBuilder;
import de.griffel.confluence.plugins.plantuml.type.GraphBuilder.Defaults;
import de.griffel.confluence.plugins.plantuml.type.GraphBuilder.NodeShape;
import de.griffel.confluence.plugins.plantuml.type.GraphBuilder.NodeStyle;

/**
 * Supported Flowchart Macro parameters.
 */
public final class FlowChartMacroParams {

   public enum Param {
      edgeArrowSize,
      nodeShape,
      nodeStyle,
      nodeFillColor,
      nodeFontname,
      nodeFontsize,
      debug;
   }

   private final Map<String, String> params;

   public FlowChartMacroParams(Map<String, String> params) {
      this.params = params;
   }

   public BigDecimal getEdgeArrowSize() {
      final BigDecimal value = getAsBigDecimal(Param.edgeArrowSize);
      return value != null ? value : GraphBuilder.Defaults.EDGE_ARROW_SIZE;
   }

   public NodeShape getNodeShape() {
      final String value = get(Param.nodeShape);
      return value != null ? NodeShape.valueOf(value) : NodeShape.DEFAULT;
   }

   public NodeStyle getNodeStyle() {
      final String value = get(Param.nodeStyle);
      return value != null ? NodeStyle.valueOf(value) : NodeStyle.DEFAULT;
   }

   public String getNodeFillColor() {
      final String value = get(Param.nodeFillColor);
      return value != null ? value : Defaults.NODE_FILL_COLOR;
   }

   public String getNodeFontname() {
      final String value = get(Param.nodeFontname);
      return value != null ? value : Defaults.NODE_FONTNAME;
   }

   public BigDecimal getNodeFontsize() {
      final BigDecimal value = getAsBigDecimal(Param.nodeFontsize);
      return value != null ? value : GraphBuilder.Defaults.NODE_FONTSIZE;
   }

   public boolean isDebug() {
      final String debug = get(Param.debug);
      return debug != null ? Boolean.valueOf(debug) : false;
   }

   @Override
   public String toString() {
      return "FlowChartMacroParams [_params=" + params + "]";
   }

   private BigDecimal getAsBigDecimal(Param param) {
      final String value = get(param);
      BigDecimal result = null;
      if (value != null) {
         try {
            result = new BigDecimal(value);
         } catch (NumberFormatException e) {
            result = null;
         }
      }
      return result;
   }

   private String get(Param param) {
      return params.get(param.name());
   }

}
