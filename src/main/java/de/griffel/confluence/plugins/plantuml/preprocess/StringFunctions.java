package de.griffel.confluence.plugins.plantuml.preprocess;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.collect.Lists;

/**
 * Immutable class to hold a list of {@link LineFunction}s.
 */
public class StringFunctions implements LineFunction {
   private final List<LineFunction> _functions;

   private StringFunctions(List<LineFunction> functions) {
      _functions = functions;
   }

   public String apply(PreprocessingContext context, String line) throws IOException, MacroException {
      String result = line;
      for (LineFunction function : _functions) {
         result = function.apply(context, result);
      }
      return result;
   }

   @Override
   public String toString() {
      return "StringFunctions [_functions=" + _functions + "]";
   }

   public static StringFunctions.Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private final List<LineFunction> _functions = Lists.newArrayList();

      public StringFunctions.Builder add(LineFunction function) {
         _functions.add(function);
         return this;
      }

      public StringFunctions build() {
         return new StringFunctions(Collections.unmodifiableList(_functions));
      }
   }
}
