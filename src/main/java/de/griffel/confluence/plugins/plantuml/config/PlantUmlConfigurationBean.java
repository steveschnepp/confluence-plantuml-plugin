package de.griffel.confluence.plugins.plantuml.config;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Java Bean holding the {@link PlantUmlConfiguration} properties.
 */
public final class PlantUmlConfigurationBean implements Serializable, PlantUmlConfiguration {
   private static final long serialVersionUID = 1L;

   private boolean svek = true;
   private String commonHeader = StringUtils.EMPTY;
   private String commonFooter = StringUtils.EMPTY;

   public boolean isSvek() {
      return svek;
   }

   public void setSvek(boolean bool) {
      svek = bool;
   }

   public String getCommonHeader() {
      return commonHeader;
   }

   public void setCommonHeader(String commonHeader) {
      this.commonHeader = commonHeader;
   }

   public boolean isSetCommonHeader() {
      return !StringUtils.isEmpty(commonHeader);
   }

   public String getCommonFooter() {
      return commonFooter;
   }

   public void setCommonFooter(String commonFooter) {
      this.commonFooter = commonFooter;
   }

   public boolean isSetCommonFooter() {
      return !StringUtils.isEmpty(commonFooter);
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("PlantUmlConfigurationBean [svek=");
      sb.append(svek);
      sb.append(", commonHeader=");
      sb.append(commonHeader);
      sb.append(", commonFooter=");
      sb.append(commonFooter);
      sb.append("]");
      return sb.toString();
   }

}
