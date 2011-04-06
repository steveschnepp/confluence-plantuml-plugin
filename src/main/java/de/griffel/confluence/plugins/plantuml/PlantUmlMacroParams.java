/*
 * Copyright 2011 avono AG. All rights reserved.
 */
package de.griffel.confluence.plugins.plantuml;

import java.util.Collections;
import java.util.Map;

/**
 * Supported PlantUML Macro parameters.
 */
public class PlantUmlMacroParams {

   public enum Param {
      title
   }

   @SuppressWarnings("rawtypes")
   private final Map _params;

   @SuppressWarnings("rawtypes")
   public PlantUmlMacroParams(Map params) {
      _params = params != null ? params : Collections.EMPTY_MAP;
   }

   public String getTitle() {
      return (String) _params.get(Param.title);
   }

   /*
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "PlantUmlMacroParams [_params=" + _params + "]";
   }

}
