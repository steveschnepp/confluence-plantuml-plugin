package de.griffel.confluence.plugins.plantuml.config;

import java.io.Serializable;

/**
 * Java Bean holding the {@link PlantUmlConfiguration} properties.
 */
public final class PlantUmlConfigurationBean implements Serializable, PlantUmlConfiguration {
   private static final long serialVersionUID = 1L;

   private boolean svek = true;

   public boolean isSvek() {
      return svek;
   }

   public void setSvek(boolean bool) {
      svek = bool;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("PlantUmlConfigurationBean [svek=");
      sb.append(svek);
      sb.append("]");
      return sb.toString();
   }

}
