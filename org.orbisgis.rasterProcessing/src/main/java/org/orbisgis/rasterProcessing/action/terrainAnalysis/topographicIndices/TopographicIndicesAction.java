/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
package org.orbisgis.rasterProcessing.action.terrainAnalysis.topographicIndices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.topographicIndices.LSFactor;
import org.grap.processing.operation.topographicIndices.StreamPowerIndex;
import org.grap.processing.operation.topographicIndices.WetnessIndex;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.rasterProcessing.action.utilities.AbstractGray16And32Process;
import org.orbisgis.ui.sif.RasterLayerCombo;
import org.sif.UIFactory;
import org.sif.multiInputPanel.CheckBoxChoice;
import org.sif.multiInputPanel.MultiInputPanel;

public class TopographicIndicesAction extends AbstractGray16And32Process
		implements ILayerAction {

	private static final Integer WETNESS = 1;

	private static final Integer STREAMPOWERINDEX = 2;

	private static final Integer LSFACTOR = 3;

	private static final Integer DEFAULT = 0;

	private MultiInputPanel mip;

	private ArrayList<Integer> indexIndice;

	private MapContext mapContext;

	private ILayer rasterAccflow;

	private GeoRaster grAccflow;

	private ILayer rasterSlope;

	private GeoRaster grSlope;

	public void execute(MapContext mapContext, ILayer resource) {
		try {
			
			this.mapContext = mapContext;
			initUIPanel();

			
			
			if (isChecked()) {

				
				grSlope = rasterSlope.getRaster();
				
				grAccflow = rasterAccflow.getRaster();
				
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService("org.orbisgis.DataManager")).getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService("org.orbisgis.DataManager");

				for (int i = 0; i < indexIndice.size(); i++) {

					Integer value = indexIndice.get(i);

					if (value == WETNESS) {

						final Operation opwetness = new WetnessIndex(
								grAccflow);
						final GeoRaster grwetness = grSlope
								.doOperation(opwetness);
						grwetness.save(tempFile);
						final ILayer newLayer = dataManager
								.createLayer(new File(tempFile));

						newLayer.setName("Wetness");
						mapContext.getLayerModel().insertLayer(newLayer, 0);

					} else if (value == STREAMPOWERINDEX) {
						
						final Operation opwetness = new StreamPowerIndex(
								grAccflow);
						final GeoRaster grwetness = grSlope
								.doOperation(opwetness);
						grwetness.save(tempFile);
						final ILayer newLayer = dataManager
								.createLayer(new File(tempFile));

						newLayer.setName("Wetness");
						mapContext.getLayerModel().insertLayer(newLayer, 0);
						
					} else if (value == LSFACTOR) {
						
						final Operation opwetness = new LSFactor(
								grAccflow);
						final GeoRaster grwetness = grSlope
								.doOperation(opwetness);
						grwetness.save(tempFile);
						final ILayer newLayer = dataManager
								.createLayer(new File(tempFile));

						newLayer.setName("Wetness");
						mapContext.getLayerModel().insertLayer(newLayer, 0);

					} else {
					}

				}

			}
		} catch (GeoreferencingException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot compute " + getClass().getName() + ": "
							+ resource.getName(), e);
		}
	}

	private void initUIPanel() throws DriverException {
		mip = new MultiInputPanel("Topographic indices");
		
		mip.addInput("slope", "Slope grid",new RasterLayerCombo(mapContext));

		mip.addInput("accflow", "Accumulation grid",new RasterLayerCombo(mapContext));
		
		
		mip.addInput("wetness", "Wetness", null, new CheckBoxChoice(true));
		mip.addInput("streampowerindex", "Stream power index", null,
				new CheckBoxChoice(true));
		mip.addInput("lsfactor", "LS factor", null, new CheckBoxChoice(true));

		mip.group("Data", new String[]{"slope","accflow"});		
		mip.group("Indices", new String[]{"wetness", "streampowerindex","lsfactor"});
		
		//TODO Talk with fergonco
		/*mip.addValidationExpression(
				"wetness=true or streampowerindex=true or lsfactor=true",
				"At leat one indice must be checked");*/
	}

	private boolean isChecked() {

		if (UIFactory.showDialog(mip)) {


			 rasterAccflow = mapContext.getLayerModel()
					.getLayerByName(mip.getInput("accflow"));
			 
			 rasterSlope = mapContext.getLayerModel()
				.getLayerByName(mip.getInput("slope"));
		
			
			indexIndice = new ArrayList<Integer>();
			
			if (new Boolean(mip.getInput("wetness"))) {
				indexIndice.add(WETNESS);
				return true;

			} else if (new Boolean(mip.getInput("streampowerindex"))) {
				indexIndice.add(STREAMPOWERINDEX);
				return true;
			} else if (new Boolean(mip.getInput("lsfactor"))) {
				indexIndice.add(LSFACTOR);
				return true;
			} else {
				indexIndice.add(DEFAULT);
			}
		} else {
			return false;

		}
		return false;
	}

	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, GeoreferencingException, IOException {

		return null;
	}

}