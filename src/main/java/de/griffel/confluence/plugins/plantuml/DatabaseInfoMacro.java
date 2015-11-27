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

import com.atlassian.confluence.languages.LocaleManager;
import java.util.Map;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

/**
 * This is the {DatabaseInfo} Macro (Confluence < 4.0).
 */
public class DatabaseInfoMacro extends BaseMacro {

    private final I18NBeanFactory _i18NBeanFactory;
    private final LocaleManager _localeManager;

    public DatabaseInfoMacro(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        _i18NBeanFactory = i18NBeanFactory;
        _localeManager = localeManager;
    }

    @SuppressWarnings("unchecked")
    public String execute(Map params, String body, RenderContext context) throws MacroException {
        return new AbstractDatabaseInfoMacroImpl(_i18NBeanFactory, _localeManager) {
            @Override
            protected String executeMyMacro(Map<String, String> params, RenderContext context)
                    throws MacroException {
                return createResult(params, (PageContext) context);
            }
        }.execute(params, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.atlassian.renderer.v2.macro.Macro#getBodyRenderMode()
     */
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

}
