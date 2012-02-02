package de.griffel.confluence.plugins.plantuml.config;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Bean holding the {@link PlantUmlConfiguration} properties.
 */
public final class PlantUmlConfigurationBean implements Serializable, PlantUmlConfiguration {
   private static final long serialVersionUID = 1L;
   private static final Logger logger = LoggerFactory.getLogger(PlantUmlConfigurationBean.class);

   private boolean _svek = true;

   public boolean isSvek() {
      return _svek;
   }

   public void setSvek(boolean bool) {
      _svek = bool;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("PlantUmlConfigurationBean [svek=");
      sb.append(_svek);
      sb.append("]");
      return sb.toString();
   }

}
