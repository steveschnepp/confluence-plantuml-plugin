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

import com.atlassian.confluence.spaces.Space;
import com.atlassian.renderer.v2.macro.MacroException;

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;

/**
 * Replaces an URL from MediaWiki syntax or Confluence syntax with a Confluence link.
 * <p>
 * URLs which includes a protocol (absolute URLs) are not replaces. Same rule applies to URLs that start with an '/'.
 */
public class UrlReplaceFunction implements LineFunction {

   /**
    * Syntax is based on UrlComannd but it is also allowed to have a link that is embedded with only one bracket
    * (Confluence syntax) instead of two brackets (MediaWiki syntax).
    * <table border="1">
    * <tr>
    * <td>MediaWiki Syntax</td>
    * <td><tt>[[URL|alias]]</tt></td>
    * </tr>
    * <tr>
    * <td>Conflunece Syntax</td>
    * <td><tt>[alias|URL]</tt></td>
    * </tr>
    * </table>
    * Besides the difference between of the brackets the Confluence alias comes <b>before</b> the URL. Both use the pipe
    * symbol '|' to separate the URL from the alias.
    */
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
            result = transformUrl(context, matcher, line);
         } else {
            result = line;
         }
      }
      return result;
   }

   static String transformUrl(PreprocessingContext context, final Matcher matcher, final String line)
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
         } else { // w/o alias
            url = matcher.group(1);
            alias = null;
         }
      }
      return renderUrl(context, line, url, alias);
   }

   private static String renderUrl(PreprocessingContext context, final String line, final String url, final String alias)
         throws MacroException {
      final ConfluenceLink link = new ConfluenceLink.Parser(context.getPageContext()).parse(url);

      final StringBuilder sb = new StringBuilder();
      sb.append("[[");
      sb.append(link.toDisplayUrl(context.getBaseUrl()));
      if (alias != null) {
         sb.append("|");
         sb.append(alias);
      } else {
         // PUML-13: nicer move-over text for Confluence URL where no alias was given
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
      // replace original URL with transformed URL
      final String result = line.replaceAll("\\[.*\\]", sb.toString());
      return result;
   }
}
