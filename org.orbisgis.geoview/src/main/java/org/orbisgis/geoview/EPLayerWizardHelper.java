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
package org.orbisgis.geoview;

import java.util.ArrayList;

import org.orbisgis.core.wizards.WizardAndId;
import org.orbisgis.core.wizards.WizardGetter;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.ChoosePanel;
import org.orbisgis.tools.ViewContext;
import org.sif.UIFactory;

public class EPLayerWizardHelper {

	public static void openWizard(GeoView2D geoview) {
		ArrayList<WizardAndId<INewLayer>> wizards = getWizards(null);
		String[] names = new String[wizards.size()];
		String[] ids = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getWizard().getName();
			ids[i] = wizards.get(i).getId();
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names, ids);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			runWizard(geoview, wizards.get(index).getWizard());
		}
	}

	private static ILayer[] runWizard(GeoView2D geoview, INewLayer wizard) {
		ILayer[] layers = wizard.getLayers();
		ViewContext vc = geoview.getViewContext();
		ILayer lc = vc.getRootLayer();
		for (ILayer layer : layers) {
			try {
				lc.put(layer);
			} catch (CRSException e) {
				PluginManager.error("The new layer CRS does not "
						+ "have the same CRS as the existing layers", e);
			} catch (LayerException e) {
				PluginManager.error("Cannot add the layer in " + lc.getName(),
						e);
			}
		}
		return layers;
	}

	public static ILayer[] runWizard(GeoView2D geoview, String wizardId) {
		ArrayList<WizardAndId<INewLayer>> wizards = getWizards(wizardId);
		return runWizard(geoview, wizards.get(0).getWizard());
	}

	private static ArrayList<WizardAndId<INewLayer>> getWizards(String id) {
		WizardGetter<INewLayer> wg = new WizardGetter<INewLayer>(
				"org.orbisgis.geoview.NewLayerWizard");
		return wg.getWizards(id);
	}

}
