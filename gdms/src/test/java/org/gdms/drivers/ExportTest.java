package org.gdms.drivers;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Geometry;

public class ExportTest extends AbstractDBTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		deleteTable(getH2Source("h2landcoverfromshp"));
		deleteTable(getPostgreSQLSource("pglandcoverfromshp"));
	}

	public void testSHP2H22PostgreSQL2SHP_2D() throws Exception {
		testSHP2H22PostgreSQL2SHP("select register('../../datas2tests/"
				+ "shp/mediumshape2D/landcover2000.shp', "
				+ "'landcover2000');", "gid", 2);
	}

	public void testSHP2H22PostgreSQL2SHP_3D() throws Exception {
		testSHP2H22PostgreSQL2SHP(
				"select register('src/test/resources/p3d.shp', "
						+ "'landcover2000');", "gid", 3);
	}

	private void testSHP2H22PostgreSQL2SHP(String script, String orderField,
			int dim) throws Exception {
		script += "select register('h2','', '0', "
				+ "'src/test/resources/backup/h2landcoverfromshp',"
				+ "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
		script += "create table h2landcoverfromshp as select * from landcover2000;";

		script += "select register('postgresql','127.0.0.1', '5432', "
				+ "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
		if (dim == 2) {
			script += "create table pglandcoverfromshp as "
					+ "select * from h2landcoverfromshp;";
		} else {
			script += "create table pglandcoverfromshp as "
					+ "select constraint3d(the_geom), gid from h2landcoverfromshp;";
		}

		script += "select register('src/test/resources/backup/landcoverfrompg.shp', 'res');";
		script += "create table res as select * from pglandcoverfromshp;";
		check(script, orderField);
	}

	private void check(String script, String orderField)
			throws SemanticException, DriverException, ParseException,
			DataSourceCreationException {
		executeGDMSScript(script);

		DataSource dsRes = dsf.getDataSourceFromSQL("select the_geom"
				+ " from res order by " + orderField);
		DataSource ds = dsf.getDataSourceFromSQL("select the_geom"
				+ " from landcover2000 order by " + orderField);
		ds.open();
		dsRes.open();
		DimensionConstraint dc1 = (DimensionConstraint) ds.getMetadata()
				.getFieldType(0).getConstraint(Constraint.GEOMETRY_DIMENSION);
		DimensionConstraint dc2 = (DimensionConstraint) dsRes.getMetadata()
				.getFieldType(0).getConstraint(Constraint.GEOMETRY_DIMENSION);
		assertTrue((dc2 == null) || (dc1 == null)
				|| (dc1.getDimension() == dc2.getDimension()));
		for (int i = 0; i < ds.getRowCount(); i++) {
			Value v1 = ds.getFieldValue(i, 0);
			Geometry g1 = v1.getAsGeometry();
			Value v2 = dsRes.getFieldValue(i, 0);
			Geometry g2 = v2.getAsGeometry();

			if (dc1.getDimension() == 2) {
				assertTrue(g1.equals(g2));
			} else {
				assertTrue(v1.equals(v2).getAsBoolean());
			}
		}
		ds.cancel();
		dsRes.cancel();
	}

	public void testSHP2PostgreSQL2H22SHP_2D() throws Exception {
		testSHP2PostgreSQL2H22SHP("select register('../../datas2tests/shp/"
				+ "mediumshape2D/landcover2000.shp', " + "'landcover2000');",
				"gid", 2);
	}

	public void testSHP2PostgreSQL2H22SHP_3D() throws Exception {
		testSHP2PostgreSQL2H22SHP(
				"select register('src/test/resources/p3d.shp', "
						+ "'landcover2000');", "gid", 3);
	}

	private void testSHP2PostgreSQL2H22SHP(String script, String orderField,
			int dim) throws Exception {
		script += "select register('postgresql','127.0.0.1', '5432', "
				+ "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
		script += "create table pglandcoverfromshp as select * from landcover2000;";

		script += "select register('h2','', '0', "
				+ "'src/test/resources/backup/h2landcoverfromshp',"
				+ "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
		script += "create table h2landcoverfromshp as select * from pglandcoverfromshp;";

		script += "select register('src/test/resources/backup/landcoverfrompg.shp', 'res');";
		if (dim == 2) {
			script += "create table res as "
					+ "select * from h2landcoverfromshp;";
		} else {
			script += "create table res as "
					+ "select constraint3d(the_geom), gid from h2landcoverfromshp;";
		}
		check(script, orderField);
	}

	public void testSHP3D2H2() throws Exception {
		String script = "select register('src/test/resources/p3d.shp', "
				+ "'landcover2000');";

		script += "select register('h2','', '0', "
				+ "'src/test/resources/backup/h2landcoverfromshp',"
				+ "'sa','','h2landcoverfromshp', 'res');";
		script += "create table res as select * from landcover2000;";
		check(script, "gid");
	}

	private void executeGDMSScript(String script) throws SemanticException,
			DriverException, ParseException {
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		String[] instructions = sqlProcessor.getScriptInstructions(script);
		for (String instruction : instructions) {
			try {
				sqlProcessor.execute(instruction, null);
			} catch (ExecutionException e) {
				throw new RuntimeException("Error in " + instruction, e);
			} catch (SemanticException e) {
				throw new RuntimeException("Error in " + instruction, e);
			} catch (DriverException e) {
				throw new RuntimeException("Error in " + instruction, e);
			}
		}
	}

}
