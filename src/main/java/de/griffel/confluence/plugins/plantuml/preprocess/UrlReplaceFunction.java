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

import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink;
import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink.NoSuchPageException;
import de.griffel.confluence.plugins.plantuml.type.ConfluenceLink.NoSuchSpaceException;

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

   public final String apply(PreprocessingContext context, final String line) throws PreprocessingException {
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
         throws PreprocessingException {
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
         throws PreprocessingException {
      final ConfluenceLink.Parser parser =
            new ConfluenceLink.Parser(context.getPageContext(), context.getSpaceManager(), context.getPageManager());
      final ConfluenceLink link;
      try {
         link = parser.parse(url);
      } catch (NoSuchPageException e) {
         throw new PreprocessingException(line, e.getMessage(), e);
      } catch (NoSuchSpaceException e) {
         throw new PreprocessingException(line, e.getMessage(), e);
      }
      final UrlRenderer urlRenderer = selectUrlRenderer(context, url);

      final StringBuilder sb = new StringBuilder();
      sb.append("[[");
      sb.append(urlRenderer.getHyperlink(link));
      sb.append("{");
      if (alias != null) {
         sb.append(alias);
      } else {
         // PUML-13: nicer move-over text for Confluence URL where no alias was given
         sb.append(urlRenderer.getDefaultAlias(context, link));
      }
      sb.append("}");
      sb.append("]]");
      // replace original URL with transformed URL
      final String result = line.replaceAll("\\[.*\\]", sb.toString());
      return result;
   }

   private static UrlRenderer selectUrlRenderer(PreprocessingContext context, final String url) {
      final UrlRenderer urlRenderer;
      if (url.startsWith(ConfluenceLink.Parser.FRAGMENT_SEPARATOR)) {
         urlRenderer = new UrlOnSamePageUrlRenderer(context.getPageAnchorBuilder());
      } else if (url.contains(ConfluenceLink.SHORTCUT_LINK_SEPARATOR)) {
         urlRenderer = new ShortcutLinkUrlRenderer(context.getShortcutLinks());
      } else {
         urlRenderer = new ExternalUrlRenderer(context.getBaseUrl(), context.getPageAnchorBuilder());
      }
      return urlRenderer;
   }
}
