package tripleo.elijah.stages.deduce.fluffy.impl;

import tripleo.elijah.stages.deduce.fluffy.i.FluffyModuleSource;

import java.io.File;

public class FluffyModuleSourceFile implements FluffyModuleSource {
	// private final tripleo.wrap.File codeFromWrapFile;
	private final java.io.File codeFromJavaFile;

	public FluffyModuleSourceFile(final File aCodeFromJavaFile) {
		codeFromJavaFile = aCodeFromJavaFile;
	}
}
