package gov.nasa.arc.mct.dbpersistence.search;

import javax.swing.JComponent;

import gov.nasa.arc.mct.services.component.SearchProvider;

public final class DatabaseSearchProvider implements SearchProvider {

	private static final String NAME = "Search Name";
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JComponent createSearchUI() {
		return new DatabaseSearchUI();
	}

}
