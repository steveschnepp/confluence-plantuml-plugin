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
package de.griffel.confluence.plugins.plantuml.type;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class GraphBuilder {

   public enum NodeShape {
      rect, ellipse, tab, note, component, folder, box3d, square;
   }

   public enum NodeStyle {
      filled, invisible, diagonals, rounded, dashed, dotted, solid, bold;
   }

   private static final String NEWLINE = System.getProperty("line.separator");

   // edge styling
   private BigDecimal edgeArrowSize = new BigDecimal("0.8");

   // node styling
   private GraphBuilder.NodeShape nodeShape = NodeShape.rect;
   private GraphBuilder.NodeStyle nodeStyle = NodeStyle.filled;
   private String nodeFillColor = "lightyellow";
   private String nodeFontname = "Verdana";
   private BigDecimal nodeFontsize = BigDecimal.valueOf(9L);

   private final StringBuilder graph = new StringBuilder();

   public GraphBuilder withEdgeArrowSize(BigDecimal arrowSize) {
      edgeArrowSize = arrowSize;
      return this;
   }

   public GraphBuilder withNodeShape(GraphBuilder.NodeShape nodeShape) {
      this.nodeShape = nodeShape;
      return this;
   }

   public GraphBuilder withNodeStyle(GraphBuilder.NodeStyle nodeStyle) {
      this.nodeStyle = nodeStyle;
      return this;
   }

   public GraphBuilder withNodeFillColor(String color) {
      nodeFillColor = color;
      return this;
   }

   public GraphBuilder withNodeFontname(String fontname) {
      nodeFontname = fontname;
      return this;
   }

   public GraphBuilder withNodeFontsize(BigDecimal fontsize) {
      nodeFontsize = fontsize;
      return this;
   }

   public GraphBuilder appendGraph(String graphOrPartialGraph) {
      graph.append(graphOrPartialGraph);
      graph.append(NEWLINE);
      return this;
   }

   public String build() {
      final StringBuilder sb = new StringBuilder();
      sb.append("digraph g {");
      sb.append(NEWLINE);
      sb.append("edge [");
      sb.append(buildEdgeParam());
      sb.append("];");
      sb.append(NEWLINE);
      sb.append("node [");
      sb.append(buildNodeParam());
      sb.append("];");
      sb.append(NEWLINE);
      sb.append(graph.toString());
      sb.append("}");
      sb.append(NEWLINE);
      return sb.toString();
   }

   private String buildNodeParam() {
      return ParamList.of(
            new Param("shape", nodeShape),
            new Param("style", nodeStyle),
            new Param("fillcolor", nodeFillColor),
            new Param("fontname", nodeFontname),
            new Param("fontsize", nodeFontsize)
            ).toString();
   }

   private String buildEdgeParam() {
      return ParamList.of(
            new Param("arrowsize", edgeArrowSize)
            ).toString();
   }

   private static class ParamList {
      private final List<Param> list = Lists.newLinkedList();

      public static ParamList of(Param... params) {
         final ParamList result = new ParamList();
         result.append(Arrays.asList(params));
         return result;
      }

      public void append(Collection<Param> params) {
         list.addAll(params);
      }

      @Override
      public String toString() {
         return Joiner.on(", ").skipNulls().join(list);
      }
   }

   private static class Param {
      private final String param;
      private final Object value;

      private Param(String param, Object value) {
         this.param = param;
         this.value = value;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append(param);
         sb.append("=\"");
         sb.append(String.valueOf(value));
         sb.append("\"");
         return sb.toString();
      }
   }
}
