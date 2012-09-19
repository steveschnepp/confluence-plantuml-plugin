package de.griffel.confluence.plugins.plantuml.config;

/**
 * Configuration properties.
 */
public interface PlantUmlConfiguration {

   boolean isSvek();

   void setSvek(boolean bool);

   String getCommonHeader();

   void setCommonHeader(String commonHeader);

   boolean isSetCommonHeader();

   String getCommonFooter();

   void setCommonFooter(String commonFooter);

   boolean isSetCommonFooter();
}
