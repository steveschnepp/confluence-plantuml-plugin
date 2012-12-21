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

import net.sourceforge.plantuml.DiagramType;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.type.GraphBuilder;

/**
 * This is the abstract implementation class of the flowchart macro.
 */
abstract class AbstractFlowChartMacroImpl {
   public String execute(Map<String, String> params, String body, RenderContext context) throws MacroException {
      final FlowChartMacroParams macroParams = new FlowChartMacroParams(params);

      final GraphBuilder graphBuilder = new GraphBuilder().appendGraph(body.trim())
            .withEdgeArrowSize(macroParams.getEdgeArrowSize())
            .withNodeShape(macroParams.getNodeShape())
            .withNodeStyle(macroParams.getNodeStyle())
            .withNodeFillColor(macroParams.getNodeFillColor())
            .withNodeFontname(macroParams.getNodeFontname())
            .withNodeFontsize(macroParams.getNodeFontsize());

      final String dotString = graphBuilder.build();

      params.put(PlantUmlMacroParams.Param.type.name(), DiagramType.DOT.name());
      if (macroParams.isDebug()) {
         params.put(PlantUmlMacroParams.Param.debug.name(), Boolean.TRUE.toString());
      }

      return executePlantUmlMacro(params, dotString, context);
   }

   protected abstract String executePlantUmlMacro(Map<String, String> params, String dotString, RenderContext context)
         throws MacroException;
}
