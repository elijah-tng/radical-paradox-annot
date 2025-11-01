package tripleo.elijah.stages.deduce.fluffy.impl;

import tripleo.elijah.stages.deduce.fluffy.i.FluffyModuleSource;

public class FluffyModuleSourceString implements FluffyModuleSource {
	private final String codeAsString;

	public FluffyModuleSourceString(final String aCodeAsString) {
		codeAsString = aCodeAsString;
	}
}
