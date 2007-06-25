package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class Dimension implements Function {

	public Function cloneFunction() {
		
		return null;
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		return ValueFactory.createValue(gv.getGeom().getDimension());
	}

	public String getName() {
		
		return "Dimension";
	}

	public int getType(int[] paramTypes) {
		
		return  Type.INT;
	}

	public boolean isAggregate() {
		
		return false;
	}

}
