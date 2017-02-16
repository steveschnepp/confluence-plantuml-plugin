package de.griffel.confluence.plugins.plantuml.preprocess;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Immutable class to hold a list of {@link LineFunction}s.
 */
public final class StringFunctions implements LineFunction {
   private final List<LineFunction> functions;

   private StringFunctions(List<LineFunction> functions) {
      this.functions = functions;
   }

   public String apply(PreprocessingContext context, String line) throws IOException, PreprocessingException {
      String result = line;
      for (LineFunction function : functions) {
         result = function.apply(context, result);
      }
      return result;
   }

   @Override
   public String toString() {
      return "StringFunctions [ffunctions=" + functions + "]";
   }

   public static StringFunctions.Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private final List<LineFunction> functions = Lists.newArrayList();

      public StringFunctions.Builder add(LineFunction function) {
         functions.add(function);
         return this;
      }

      public StringFunctions build() {
         return new StringFunctions(Collections.unmodifiableList(functions));
      }
   }
}
