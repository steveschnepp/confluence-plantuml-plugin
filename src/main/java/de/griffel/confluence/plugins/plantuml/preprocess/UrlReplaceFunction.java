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

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.CommandControl;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Replaces the URL as defined in the {@link CommandUrl} class from Confluence link style with a Confluence link.
 * <p>
 * Note: The link is only replaced if it is nor a relative link or absolute link.
 */
public class UrlReplaceFunction implements LineFunction {

   private static final String URL_PATTERN_REGEX = "\\[\\[([^|]*)(?:\\|([^|]*))?\\]\\]";
   private static final Pattern URL_PATTERN = Pattern.compile(URL_PATTERN_REGEX);

   public String apply(PreprocessingContext context, final String line) {
      final CommandUrl myCommandUrl = new CommandUrl(new DummyEntityDiagram());
      final CommandControl control = myCommandUrl.isValid(Collections.singletonList(line));

      final String result;
      switch (control) {
      case OK:
         final Matcher matcher = URL_PATTERN.matcher(line);
         if (!matcher.find()) {
            result = line;
         } else {
            // check for absolute or relative links
            if (!(line.contains("://") || line.contains("[[/"))) {
               result = toConfluenceUrl(context, matcher, line);
            } else {
               result = line;
            }
         }
         break;
      default:
         result = line;
      }
      return result;
   }

   static String toConfluenceUrl(PreprocessingContext context, final Matcher matcher, final String line) {
      final String url = matcher.group(1);
      final String alias = matcher.group(2);
      final ConfluenceLink link = new ConfluenceLink.Parser(new PageContextMock()).parse(url);
      final StringBuilder sb = new StringBuilder();
      sb.append("[[");
      sb.append(link.toDisplayUrl(context.getBaseUrl()));
      if (alias != null) {
         sb.append("|");
         sb.append(alias);
      }
      sb.append("]]");
      final String result = line.replaceAll(URL_PATTERN_REGEX, sb.toString());
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
