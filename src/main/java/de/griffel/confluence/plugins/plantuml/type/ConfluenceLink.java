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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents a Confluence link to a page or attachment. Note: The name of the attachment and section is optional.
 */
public final class ConfluenceLink implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String spaceKey;
   private final String pageTitle;
   private final String attachmentName;
   private final String fragment;

   /**
    * Separator string for shortcut links.
    */
   public static final String SHORTCUT_LINK_SEPARATOR = "@";

   public ConfluenceLink(String spaceKey, String pageTitle, String attachmentName, String fragment) {
      Preconditions.checkNotNull(spaceKey);
      Preconditions.checkNotNull(pageTitle);
      this.spaceKey = spaceKey;
      this.pageTitle = pageTitle;
      this.attachmentName = attachmentName;
      this.fragment = fragment;

      if (fragment != null && attachmentName != null) {
         throw new IllegalArgumentException(
               "Either attachment name or fragment can be set but not both: attachment name is '" + attachmentName
                     + "' and fragment is '" + fragment + "'");
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(getSpaceKey(), getPageTitle(), getAttachmentName(), getFragment());
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
      return getFragment() != null;
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
            && Objects.equal(getFragment(), other.getFragment());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      if (StringUtils.isNotEmpty(getSpaceKey())) {
         sb.append(getSpaceKey());
         sb.append(":");
      }
      sb.append(getPageTitle());
      if (StringUtils.isNotEmpty(getAttachmentName())) {
         sb.append("^");
         sb.append(getAttachmentName());
      }
      if (StringUtils.isNotEmpty(getFragment())) {
         sb.append("#");
         sb.append(getFragment());
      }
      return sb.toString();
   }

   /**
    * Returns the space key.
    * 
    * @return the space key.
    */
   public String getSpaceKey() {
      return spaceKey;
   }

   /**
    * Returns the page title.
    * 
    * @return the page title.
    */
   public String getPageTitle() {
      return pageTitle;
   }

   /**
    * Returns the attachment name.
    * 
    * @return the attachment name or <tt>null</tt> if this link don't reference an attachment.
    */
   public String getAttachmentName() {
      return attachmentName;
   }

   /**
    * Returns the fragment of the URL.
    * 
    * @return the fragment of the URL.
    */
   public String getFragment() {
      return fragment;
   }

   /**
    * Returns {@code true} if this link contains a shortcut link in the page title.
    * 
    * @return {@code true} if this link contains a shortcut link in the page title.
    */
   public boolean isShortCutLink() {
      return pageTitle.contains(SHORTCUT_LINK_SEPARATOR);
   }

   /**
    * Returns {@code true} if this link contains a blog post link in the page title.
    * 
    * @return {@code true} if this link contains a blog post link in the page title.
    */
   public boolean isBlogPost() {
      return pageTitle.contains("/");
   }

   public String getBlogPostTitle() {
      return StringUtils.substringAfterLast(getPageTitle(), "/");
   }

   /**
    * Returns the URL fragment of the Confluence Link. The fragment is the part of the URL after the '#".
    * 
    * @return the URL fragment of the Confluence Link. This URL always starts with the '#' and is URL-encoded. .
    */
   public String toFragmentUrl() {
      final StringBuilder sb = new StringBuilder();
      sb.append(StringUtils.deleteWhitespace(getPageTitle()));
      sb.append("-");
      sb.append(StringUtils.deleteWhitespace(getFragment()));

      final String result;
      try {
         result = "#" + URLEncoder.encode(sb.toString(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("UTF-8 encoding not supported?", e);
      }
      return result;
   }

   Calendar getBlogPostDay() {
      final String dayString = StringUtils.stripStart(
            StringUtils.substringBeforeLast(getPageTitle(), "/"), "/");
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      final Calendar day = Calendar.getInstance();
      try {
         day.setTime(sdf.parse(dayString));
      } catch (ParseException e) {
         throw new RuntimeException("Cannot parse blog post date string: " + dayString, e);
      }
      return day;
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
       * URL separator for a fragment
       */
      public static final String FRAGMENT_SEPARATOR = "#";
      private final PageContext pageContext;
      private final SpaceManager spaceManager;
      private final PageManager pageManager;

      /**
       * Constructs a new Parser. This parser validates if the link references a valid page and a valid space using the
       * given page manager and space manager.
       * 
       * @param context the page context.
       * @param spaceManager the space manager.
       * @param pageManager the page manger.
       */
      public Parser(PageContext context, SpaceManager spaceManager, PageManager pageManager) {
         pageContext = context;
         this.spaceManager = spaceManager;
         this.pageManager = pageManager;
      }

      /**
       * Constructs a new Parser. This parser does not validate if the page or space is valid.
       * 
       * @param context the page context.
       */
      public Parser(PageContext context) {
         this(context, null, null);
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
         final String pageTitleWithFragment;
         final String attachmentName;

         // link to "^attachment" ?
         if (link.contains("^")) {
            final String[] parts = link.split("[\\^:]");
            if (parts.length > 1 && !StringUtils.isEmpty(parts[0])) {
               if (parts.length > 2) {
                  spaceKey = parts[0];
                  pageTitleWithFragment = parts[1];
                  attachmentName = parts[2];
               } else {
                  spaceKey = pageContext.getSpaceKey();
                  pageTitleWithFragment = parts[0];
                  attachmentName = parts[1];
               }
            } else {
               spaceKey = pageContext.getSpaceKey();
               pageTitleWithFragment = pageContext.getPageTitle();
               attachmentName = parts[1];
            }
         } else {
            attachmentName = null;
            final String[] parts = link.split(":");
            if (parts.length > 1) {
               spaceKey = parts[0];
               pageTitleWithFragment = parts[1];
            } else {
               spaceKey = pageContext.getSpaceKey();
               pageTitleWithFragment = link;
            }
         }

         // page title contains "#fragment" ?
         final String fragment;
         final String pageTitle;
         if (pageTitleWithFragment.contains(FRAGMENT_SEPARATOR)) {
            if (pageTitleWithFragment.startsWith(FRAGMENT_SEPARATOR)) {
               pageTitle = pageContext.getPageTitle();
               fragment = pageTitleWithFragment.substring(FRAGMENT_SEPARATOR.length());
            } else {
               pageTitle = StringUtils.substringBefore(pageTitleWithFragment, FRAGMENT_SEPARATOR);
               fragment = StringUtils.substringAfter(pageTitleWithFragment, FRAGMENT_SEPARATOR);
            }
         } else {
            pageTitle = pageTitleWithFragment;
            fragment = null;
         }

         final ConfluenceLink result = new ConfluenceLink(spaceKey, pageTitle, attachmentName, fragment);
         if (!result.isShortCutLink()) {
            if (spaceManager != null) {

               final Space space = spaceManager.getSpace(spaceKey);
               if (space == null) {
                  throw new NoSuchSpaceException(link, spaceKey);
               }
            }

            if (pageManager != null) {

               if (result.isBlogPost()) {
                  final BlogPost blogPost =
                        pageManager.getBlogPost(spaceKey, result.getBlogPostTitle(), result.getBlogPostDay());
                  if (blogPost == null) {
                     throw new NoSuchBlogPostException(link, spaceKey, result.getPageTitle());
                  }
               } else {
                  final Page page = pageManager.getPage(spaceKey, pageTitle);
                  if (page == null) {
                     throw new NoSuchPageException(link, spaceKey, pageTitle);
                  }
               }
            }
         }
         return result;
      }
   }

   public static class NoSuchBlogPostException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      protected NoSuchBlogPostException(String link, String spaceKey, String pageTitle) {
         super("Cannot find blog post '" + pageTitle + "' in space '" + spaceKey + "' referenced by link '" + link
               + "'.");
      }

   }

   public static class NoSuchPageException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      protected NoSuchPageException(String link, String spaceKey, String pageTitle) {
         super("Cannot find page '" + pageTitle + "' in space '" + spaceKey + "' referenced by link '" + link + "'.");
      }

   }

   public static class NoSuchSpaceException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      protected NoSuchSpaceException(String link, String spaceKey) {
         super("The space with the key '" + spaceKey + "' from link '" + link + "' does not exists.");
      }

   }
}
