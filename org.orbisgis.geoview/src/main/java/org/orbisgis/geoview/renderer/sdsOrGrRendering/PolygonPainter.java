/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.renderer.sdsOrGrRendering;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Shape;


import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PolygonStyle;

import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;

import com.vividsolutions.jts.geom.Geometry;


public class PolygonPainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final  PolygonStyle polygonStyle, final MapControl mapControl) {
		Shape liteShape;
		
		
		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
			
			if ((polygonStyle.getFillColor() != null)) {				
				g.setPaint(polygonStyle.getFillColor());
				
				g.fill(liteShape);
			}

			if (polygonStyle.getLineColor() != null) {
				g.setColor(polygonStyle.getLineColor());

			} else {
				g.setColor(polygonStyle.getDefaultLineColor());
			}

			/**
			 * todo : implements stroke
			 */
			/*	if (null != style.getStrokeColor()) {
				g.setStroke(style.getStrokeColor());
			}
			*/
							
			
			//g.setComposite(polygonStyle.getAlphaComposite());
			g.draw(liteShape);

		}
	}
}