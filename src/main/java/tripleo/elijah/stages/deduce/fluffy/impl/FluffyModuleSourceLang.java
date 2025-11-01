package tripleo.elijah.stages.deduce.fluffy.impl;

import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyModuleSource;

public class FluffyModuleSourceLang implements FluffyModuleSource {
	private final OS_Module langModule;

	public FluffyModuleSourceLang(final OS_Module aLangModule) {
		langModule = aLangModule;
	}
}
