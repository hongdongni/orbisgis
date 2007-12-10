package org.orbisgis.geocatalog.resources.db;

import org.gdms.data.db.DBSource;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class Main {
	public static void main(String[] args) {
		final FirstUIPanel firstPanel = new FirstUIPanel();
		final SecondUIPanel secondPanel = new SecondUIPanel(firstPanel);

		if (UIFactory.showDialog(new UIPanel[] { firstPanel, secondPanel })) {
			for (DBSource item : secondPanel.getSelectedDBSources()) {
				System.out.println(item.getTableName());
			}
		}
	}
}