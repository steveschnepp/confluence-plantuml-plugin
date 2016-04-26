/*
 * Copyright (C) 2011 Michael Griffel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This distribution includes other third-party libraries.
 * These libraries and their corresponding licenses (where different
 * from the GNU General Public License) are enumerated below.
 *
 * PlantUML is a Open-Source tool in Java to draw UML Diagram.
 * The software is developed by Arnaud Roques at
 * http://plantuml.sourceforge.org.
 */
package de.griffel.confluence.plugins.plantuml;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

/**
 * This is the {DatabaseInfo} Macro (Confluence > 4.0).
 */
public class DatabaseInfoMacroV4 implements Macro {

    private final I18NBeanFactory _i18NBeanFactory;
    private final LocaleManager _localeManager;

    public DatabaseInfoMacroV4(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        _i18NBeanFactory = i18NBeanFactory;
        _localeManager = localeManager;
    }

    public String execute(Map<String, String> params, String body, ConversionContext context)
            throws MacroExecutionException {
        try {
            return new AbstractDatabaseInfoMacroImpl(_i18NBeanFactory, _localeManager) {
                @Override
                protected String executeMyMacro(Map<String, String> params, RenderContext context)
                        throws MacroException {
                    return createResult(params, (PageContext) context);
                }
            }.execute(params, context.getPageContext());
        } catch (MacroException e) {
            throw new MacroExecutionException(e);
        }
    }

    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

}
