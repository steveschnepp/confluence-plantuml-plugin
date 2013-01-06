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

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource that can be either downloaded via URL or read from a stream.
 */
public interface DownloadResourceInfo {

   /**
    * Returns an input stream to read the resource.
    * 
    * @return an input stream to read the resource.
    * @throws IOException in case of an I/O problem.
    */
   InputStream getStreamForReading() throws IOException;

   /**
    * Returns the download path (URL path) to get the resource.
    * 
    * @return the download path (URL path) to get the resource.
    */
   String getDownloadPath();
}
