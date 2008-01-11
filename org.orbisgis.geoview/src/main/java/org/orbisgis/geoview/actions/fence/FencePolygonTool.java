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
package org.orbisgis.geoview.actions.fence;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPolygonTool;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class FencePolygonTool extends AbstractPolygonTool {
	private final DataSourceFactory dsf = OrbisgisCore.getDSF();

	private DataSource dsResult;

	private VectorLayer layer;

	private final String fenceLayerName = "fence";

	protected void polygonDone(Polygon g, ViewContext vc, ToolManager tm)
			throws TransitionException {
		try {
			if (null != layer) {
				vc.getRootLayer().remove(layer);
			}
			buildFenceDatasource(g);
			layer = LayerFactory.createVectorialLayer(fenceLayerName, dsResult);
			BasicStyle style = new BasicStyle(Color.orange, 10, null);

			layer.setStyle(style);

			try {
				vc.getRootLayer().put(layer);
			} catch (CRSException e) {
				PluginManager.error("Bug in fence tool", e);
			}
		} catch (LayerException e) {
			PluginManager.error("Cannot use fence tool: " + e.getMessage(), e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {

		return vc.getRootLayer().getLayerCount() > 0;
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {

		return true;
	}

	private String buildFenceDatasource(Geometry g) {

		ObjectMemoryDriver driver;
		try {
			driver = new ObjectMemoryDriver(new String[] { "the_geom" },
					new Type[] { TypeFactory.createType(Type.GEOMETRY) });

			if (!dsf.getSourceManager().exists(fenceLayerName)) {
				dsf.getSourceManager().register(fenceLayerName, driver);
			}

			dsResult = dsf.getDataSource(fenceLayerName);

			dsResult.open();

			while (dsResult.getRowCount() > 0) {
				dsResult.deleteRow(0);
			}

			if (dsResult.getFieldCount() == 0) {
				dsResult.addField("the_geom", TypeFactory
						.createType(Type.GEOMETRY));
			}

			dsResult
					.insertFilledRow(new Value[] { ValueFactory.createValue(g) });

			dsResult.commit();

			return dsResult.getName();
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (DataSourceCreationException e) {
			throw new RuntimeException(e);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		} catch (FreeingResourcesException e) {
			throw new RuntimeException(e);
		} catch (NonEditableDataSourceException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		}

	}

}
