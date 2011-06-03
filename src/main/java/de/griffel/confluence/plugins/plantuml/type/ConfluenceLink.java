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

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.renderer.PageContext;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents a Confluence link to a page or attachment. Note: The name of the attachment is optional.
 */
public class ConfluenceLink implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String _spaceKey;
   private final String _pageTitle;
   private final String _attachmentName;

   public ConfluenceLink(String spaceKey, String pageTitle, String attachmentName) {
      _spaceKey = spaceKey;
      _pageTitle = pageTitle;
      _attachmentName = attachmentName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getSpaceKey(), getPageTitle(), getAttachmentName());
   }

   public boolean hasAttachmentName() {
      return getAttachmentName() != null;
   }

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
            && Objects.equal(getAttachmentName(), other.getAttachmentName());
   }

   @Override
   public String toString() {
      return "ConfluenceLink [_spaceKey=" + getSpaceKey() + ", _pageTitle=" + getPageTitle()
            + ", _attachmentName=" + getAttachmentName() + "]";
   }

   public String getSpaceKey() {
      return _spaceKey;
   }

   public String getPageTitle() {
      return _pageTitle;
   }

   public String getAttachmentName() {
      return _attachmentName;
   }

   public String toUrlPath() {
      final StringBuilder sb = new StringBuilder();
      sb.append(getSpaceKey());
      sb.append("/");
      sb.append(getPageTitle());
      return sb.toString();
   }

   /**
    * ConfluenceLinkParser can be used to parse a string to a {@link ConfluenceLink}. The following string
    * representations are supporte:
    * 
    * <pre>
    * ^attachment.ext
    * or
    * pagetitle
    * pagetitle^attachment.ext
    * or
    * spacekey:pagetitle
    * spacekey:pagetitle^attachment.ext
    * </pre>
    */
   public static final class Parser {
      private final PageContext _pageContext;

      public Parser(PageContext context) {
         _pageContext = context;
      }

      public ConfluenceLink parse(String link) {
         Preconditions.checkNotNull(link);

         final String spaceKey;
         final String pageTitle;
         final String attachmentName;
         if (link.contains("^")) {
            final String[] parts = link.split("[\\^:]");
            if (parts.length > 1 && !StringUtils.isEmpty(parts[0])) {
               if (parts.length > 2) {
                  spaceKey = parts[0];
                  pageTitle = parts[1];
                  attachmentName = parts[2];
               } else {
                  spaceKey = _pageContext.getSpaceKey();
                  pageTitle = parts[0];
                  attachmentName = parts[1];
               }
            } else {
               spaceKey = _pageContext.getSpaceKey();
               pageTitle = _pageContext.getPageTitle();
               attachmentName = parts[1];
            }
         } else {
            attachmentName = null;
            final String[] parts = link.split(":");
            if (parts.length > 1) {
               spaceKey = parts[0];
               pageTitle = parts[1];
            } else {
               spaceKey = _pageContext.getSpaceKey();
               pageTitle = link;
            }
         }
         return new ConfluenceLink(spaceKey, pageTitle, attachmentName);
      }
   }

}
