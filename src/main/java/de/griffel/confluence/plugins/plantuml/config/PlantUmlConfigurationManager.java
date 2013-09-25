package de.griffel.confluence.plugins.plantuml.config;

/**
 * Configuration Manager Interface.
 */
public interface PlantUmlConfigurationManager {

   /**
    * Loads the configuration.
    */
   PlantUmlConfiguration load();

   /**
    * Persists the given configuration.
    * 
    * @param config the configuration to save.
    */
   void save(PlantUmlConfiguration config);

}
