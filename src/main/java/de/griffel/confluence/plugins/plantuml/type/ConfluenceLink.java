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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.renderer.PageContext;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents a Confluence link to a page or attachment. Note: The name of the attachment and section is optional.
 */
public final class ConfluenceLink implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String _spaceKey;
   private final String _pageTitle;
   private final String _attachmentName;
   private final String _section;

   public ConfluenceLink(String spaceKey, String pageTitle, String attachmentName, String section) {
      _spaceKey = spaceKey;
      _pageTitle = pageTitle;
      _attachmentName = attachmentName;
      _section = section;

      if (section != null && _attachmentName != null) {
         throw new IllegalArgumentException(
               "Either attachment name or section can be set but not both: attachment name is '" + attachmentName
                     + "' and section is '" + section + "'");
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(getSpaceKey(), getPageTitle(), getAttachmentName(), getSection());
   }

   /**
    * Returns <tt>true</tt> if this Confluence link references an attachment.
    * 
    * @return <tt>true</tt> if this Confluence link references an attachment; <tt>false</tt> otherwise.
    */
   public boolean hasAttachmentName() {
      return getAttachmentName() != null;
   }

   /**
    * Returns <tt>true</tt> if this Confluence link references an section within a page.
    * 
    * @return <tt>true</tt> if this Confluence link references an section within a page.; <tt>false</tt> otherwise.
    */
   public boolean hasSection() {
      return getSection() != null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (obj == null) {
         return false;
      }
      if (!(obj instanceof ConfluenceLink)) {
         return false;
      }
      final ConfluenceLink other = (ConfluenceLink) obj;
      return Objects.equal(getSpaceKey(), other.getSpaceKey())
            && Objects.equal(getPageTitle(), other.getPageTitle())
            && Objects.equal(getAttachmentName(), other.getAttachmentName())
            && Objects.equal(getSection(), other.getSection());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("ConfluenceLink [_spaceKey=");
      sb.append(getSpaceKey());
      sb.append(", _pageTitle=");
      sb.append(getPageTitle());
      sb.append(", _attachmentName=");
      sb.append(getAttachmentName());
      sb.append(", _section=");
      sb.append(getSection());
      sb.append("]");
      return sb.toString();
   }

   /**
    * Returns the space key.
    * 
    * @return the space key.
    */
   public String getSpaceKey() {
      return _spaceKey;
   }

   /**
    * Returns the page title.
    * 
    * @return the page title.
    */
   public String getPageTitle() {
      return _pageTitle;
   }

   /**
    * Returns the attachment name.
    * 
    * @return the attachment name or <tt>null</tt> if this link don't reference an attachment.
    */
   public String getAttachmentName() {
      return _attachmentName;
   }

   /**
    * Returns the section.
    * 
    * @return the section.
    */
   public String getSection() {
      return _section;
   }

   /**
    * Returns the external display URL of this Confluence link.
    * 
    * @param baseUrl
    * @param alias
    * @return
    */
   public String toDisplayUrl(String baseUrl) {
      final StringBuilder sb = new StringBuilder();
      sb.append(baseUrl);
      sb.append("/display/");
      sb.append(getSpaceKey());
      sb.append("/");
      sb.append(getPageTitle());
      if (hasSection()) {
         sb.append(buildSectionUrl());
      }
      return sb.toString();
   }

   private String buildSectionUrl() {
      final StringBuilder sb = new StringBuilder();
      sb.append(StringUtils.deleteWhitespace(getPageTitle()));
      sb.append("-");
      sb.append(StringUtils.deleteWhitespace(getSection()));

      final String result;
      try {
         result = "#" + URLEncoder.encode(sb.toString(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("UTF-8 encoding not supported?", e);
      }
      return result;
   }

   /**
    * ConfluenceLinkParser can be used to parse a string to a {@link ConfluenceLink}. The following string
    * representations are supported:
    * 
    * <pre>
    * ^attachment.ext
    * #section
    * or
    * pagetitle
    * pagetitle^attachment.ext
    * pagetitle#section
    * or
    * spacekey:pagetitle
    * spacekey:pagetitle^attachment.ext
    * spacekey:pagetitle#section
    * </pre>
    */
   public static final class Parser {
      /**
       *
       */
      private static final String SECTION_SEPARATOR = "#";
      private final PageContext _pageContext;

      public Parser(PageContext context) {
         _pageContext = context;
      }

      /**
       * Parses the string representation of a Confluence link.
       * 
       * @param link the Confluence link as string. see {@link Parser} for more information about the link syntax.
       * @return a instance of {@link ConfluenceLink} that build of the given string representation.
       */
      public ConfluenceLink parse(String link) {
         Preconditions.checkNotNull(link);

         final String spaceKey;
         final String pageTitleWithSection;
         final String attachmentName;

         // link to "^attachment" ?
         if (link.contains("^")) {
            final String[] parts = link.split("[\\^:]");
            if (parts.length > 1 && !StringUtils.isEmpty(parts[0])) {
               if (parts.length > 2) {
                  spaceKey = parts[0];
                  pageTitleWithSection = parts[1];
                  attachmentName = parts[2];
               } else {
                  spaceKey = _pageContext.getSpaceKey();
                  pageTitleWithSection = parts[0];
                  attachmentName = parts[1];
               }
            } else {
               spaceKey = _pageContext.getSpaceKey();
               pageTitleWithSection = _pageContext.getPageTitle();
               attachmentName = parts[1];
            }
         } else {
            attachmentName = null;
            final String[] parts = link.split(":");
            if (parts.length > 1) {
               spaceKey = parts[0];
               pageTitleWithSection = parts[1];
            } else {
               spaceKey = _pageContext.getSpaceKey();
               pageTitleWithSection = link;
            }
         }

         // page title contains "#section" ?
         final String section;
         final String pageTitle;
         if (pageTitleWithSection.contains(SECTION_SEPARATOR)) {
            if (pageTitleWithSection.startsWith(SECTION_SEPARATOR)) {
               pageTitle = _pageContext.getPageTitle();
               section = pageTitleWithSection.substring(SECTION_SEPARATOR.length());
            } else {
               pageTitle = StringUtils.substringBefore(pageTitleWithSection, SECTION_SEPARATOR);
               section = StringUtils.substringAfter(pageTitleWithSection, SECTION_SEPARATOR);
            }
         } else {
            pageTitle = pageTitleWithSection;
            section = null;
         }
         return new ConfluenceLink(spaceKey, pageTitle, attachmentName, section);
      }
   }

}
