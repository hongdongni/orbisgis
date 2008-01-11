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
package org.orbisgis.geoview.renderer.style.sld;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class RasterSymbolizer {

	
	private VTD vtd;
	private String rootXpathQuery;

	
	/**
	 * Apply a color ramp on a DEM
	 * 
	 * 	<RasterSymbolizer>
   		<Opacity>1.0</Opacity>
   		<ColorMap>
	  <ColorMapEntry color="#00ff00" quantity="-500"/>
      <ColorMapEntry color="#00fa00" quantity="-417"/>
      <ColorMapEntry color="#14f500" quantity="-333"/>
      <ColorMapEntry color="#28f502" quantity="-250"/>
      <ColorMapEntry color="#3cf505" quantity="-167"/>
      <ColorMapEntry color="#50f50a" quantity="-83"/>
      <ColorMapEntry color="#64f014" quantity="-1"/>
      <ColorMapEntry color="#7deb32" quantity="0"/>
      <ColorMapEntry color="#78c818" quantity="30"/>
      <ColorMapEntry color="#38840c" quantity="105"/>
      <ColorMapEntry color="#2c4b04" quantity="300"/>
      <ColorMapEntry color="#ffff00" quantity="400"/>
      <ColorMapEntry color="#dcdc00" quantity="700"/>
      <ColorMapEntry color="#b47800" quantity="1200"/>
      <ColorMapEntry color="#c85000" quantity="1400"/>
      <ColorMapEntry color="#be4100" quantity="1600"/>
      <ColorMapEntry color="#963000" quantity="2000"/>
      <ColorMapEntry color="#3c0200" quantity="3000"/>
      <ColorMapEntry color="#ffffff" quantity="5000"/>
      <ColorMapEntry color="#ffffff" quantity="13000"/>
  	</ColorMap>
   	<OverlapBehavior>
      <AVERAGE/>
   	</OverlapBehavior>
   	<ShadedRelief/>
	</RasterSymbolizer>

	 *
	 *
	 */
	public RasterSymbolizer(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
	
	public int getOpacity() throws XPathParseException{
		return vtd.evalToInt(rootXpathQuery + "/sld:Opacity");
	}
	
	public ColorMap getColorMap(){
		
		return new ColorMap(vtd, rootXpathQuery + "/sld:ColorMap");
	}
	
	
public String toString(){
		
		try {
			return vtd.getContent(rootXpathQuery);
		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
