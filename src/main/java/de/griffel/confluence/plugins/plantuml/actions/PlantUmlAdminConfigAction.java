package de.griffel.confluence.plugins.plantuml.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.breadcrumbs.AdminActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;

import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfiguration;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationBean;
import de.griffel.confluence.plugins.plantuml.config.PlantUmlConfigurationManager;

/**
 * XWork action for the administration UI.
 */
public final class PlantUmlAdminConfigAction extends ConfluenceActionSupport implements BreadcrumbAware {
   private static final long serialVersionUID = 1L;

   private static final Logger logger = LoggerFactory.getLogger(PlantUmlAdminConfigAction.class);

   private PlantUmlConfigurationManager configurationManager;

   private boolean isSvek;

   public void setSvek(boolean flag) {
      isSvek = flag;
   }

   public boolean isSvek() {
      return isSvek;
   }

   public void setConfigurationManager(PlantUmlConfigurationManager configurationManager) {
      this.configurationManager = configurationManager;
   }

   /**
    * {@inheritDoc}
    */
   public String load() {
      final PlantUmlConfiguration configuration = configurationManager.load();
      isSvek = configuration.isSvek();
      logger.debug("Loaded configuration {}", configuration);
      return SUCCESS;
   }

   /**
    * {@inheritDoc}
    */
   public String save() {
      final PlantUmlConfiguration configuration = new PlantUmlConfigurationBean();
      configuration.setSvek(isSvek);
      configurationManager.save(configuration);
      logger.debug("Saved configuration {}", configuration);
      addActionMessage(getText("plantuml.admin.config.saved"));
      return SUCCESS;
   }

   /**
    * {@inheritDoc}
    */
   public Breadcrumb getBreadcrumb() {
      return new AdminActionBreadcrumb(this);
   }

}
