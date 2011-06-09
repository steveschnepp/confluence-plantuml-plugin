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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.cucadiagram.IEntity;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Replaces the URL as defined in the {@link CommandUrl} class from Confluence link style with a Confluence link.
 * <p>
 * Note: The link is only replaced if it is nor a relative link or absolute link.
 */
public class UrlReplaceFunction implements LineFunction {

   private static final String URL_LINE_REGEX = "^url\\s*(?:of|for)?\\s+(?:[\\p{L}0-9_.]+|\"[^\"]+\")\\s+(?:is)?\\s*"
         + "(?:\\[)?\\[([^|]*?)(?:\\|([^|]*?))?(?:\\])+";

   private static final Pattern URL_LINE_PATTERN = Pattern.compile(URL_LINE_REGEX);

   public String apply(PreprocessingContext context, final String line) throws MacroException {
      final String result;
      final Matcher matcher = URL_LINE_PATTERN.matcher(line);
      if (!matcher.find()) {
         result = line;
      } else {
         // check for absolute or relative links
         if (!(line.contains("://") || line.contains("[/"))) {
            result = toConfluenceUrl(context, matcher, line);
         } else {
            result = line;
         }
      }
      return result;
   }

   static String toConfluenceUrl(PreprocessingContext context, final Matcher matcher, final String line)
         throws MacroException {
      final String url;
      final String alias;
      if (line.contains("[[")) {
         // Wikipedia Syntax
         url = matcher.group(1);
         alias = matcher.group(2);
      } else {
         // Confluence Syntax
         if (line.contains("|")) {
            url = matcher.group(2);
            alias = matcher.group(1);
         } else {
            url = matcher.group(1);
            alias = null;
         }
      }
      final ConfluenceLink link = new ConfluenceLink.Parser(context.getPageContext()).parse(url);
      final StringBuilder sb = new StringBuilder();
      sb.append("[[");
      sb.append(link.toDisplayUrl(context.getBaseUrl()));
      if (alias != null) {
         sb.append("|");
         sb.append(alias);
      } else {
         // PUML-13: nicer move-over text for Confluence URL
         sb.append("|");
         final Space space = context.getSpaceManager().getSpace(link.getSpaceKey());
         if (space == null) {
            throw new MacroException("The space key '" + link.getSpaceKey() + "' from the link '" + line
                  + "' is unknown");
         }
         sb.append(space.getName());
         sb.append(" - ");
         sb.append(link.getPageTitle());
      }
      sb.append("]]");
      final String result = line.replaceAll("\\[.*\\]", sb.toString());
      return result;
   }

   private static class DummyEntityDiagram extends AbstractEntityDiagram {

      @Override
      public IEntity getOrCreateClass(String code) {
         throw new UnsupportedOperationException();
      }

      @Override
      public UmlDiagramType getUmlDiagramType() {
         throw new UnsupportedOperationException();
      }

   }
}
