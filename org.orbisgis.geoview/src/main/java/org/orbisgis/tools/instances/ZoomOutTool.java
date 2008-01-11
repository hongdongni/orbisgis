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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.generated.ZoomOut;

/**
 * Tool to zoom out
 *
 * @author Fernando Gonzalez Cortes
 */
public class ZoomOutTool extends ZoomOut {

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(ViewContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Done()
	 */
	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		Rectangle2D extent = vc.getExtent();
		double width = 2 * extent.getWidth();
		double height = 2 * extent.getHeight();
		Rectangle2D newExtent = new Rectangle2D.Double(tm.getValues()[0]
				- width / 2, tm.getValues()[1] - height / 2, width, height);
		vc.setExtent(newExtent);

		transition("init"); //$NON-NLS-1$
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Done(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomOut#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g, ViewContext vc, ToolManager tm) {
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return vc.atLeastNThemes(1);
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}
}
