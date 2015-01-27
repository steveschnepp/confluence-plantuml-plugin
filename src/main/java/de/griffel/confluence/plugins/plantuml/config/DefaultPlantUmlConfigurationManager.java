package de.griffel.confluence.plugins.plantuml.config;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;

/**
 * This class is responsible for loading and storing the configuration for this plugin.
 */
public final class DefaultPlantUmlConfigurationManager implements PlantUmlConfigurationManager {

   private final BandanaManager bandanaManager;

   public DefaultPlantUmlConfigurationManager(BandanaManager bandanaManager) {
      this.bandanaManager = bandanaManager;
   }

   public PlantUmlConfiguration load() {
      final ConfluenceBandanaContext context = new ConfluenceBandanaContext();

      PlantUmlConfiguration config = (PlantUmlConfiguration) bandanaManager.getValue(
            context, PlantUmlConfigurationBean.class.getName());

      if (config == null) {
         config = new PlantUmlConfigurationBean();
      }
      return config;
   }

   public void save(PlantUmlConfiguration config) {
      final ConfluenceBandanaContext context = new ConfluenceBandanaContext();
      bandanaManager.setValue(context, PlantUmlConfigurationBean.class.getName(), config);
   }

}
