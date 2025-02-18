package tripleo.elijah.comp;

import org.jetbrains.annotations.*;

interface CompilationChange {
	void apply(final CompilationImpl c);
}

class CC_SetStage implements CompilationChange {
	private final String s;

	@Contract(pure = true)
	public CC_SetStage(final String aS) {
		s = aS;
	}

	@Override
	public void apply(final @NotNull CompilationImpl c) {
		c.getCfg().stage = Stages.valueOf(s);
	}
}

class CC_SetShowTree implements CompilationChange {
	private final boolean flag;

	public CC_SetShowTree(final boolean aB) {
		flag = aB;
	}

	@Override
	public void apply(final CompilationImpl c) {
		c.getCfg().showTree = flag;
	}
}

class CC_SetDoOut implements CompilationChange {
	private final boolean flag;

	public CC_SetDoOut(final boolean aB) {
		flag = aB;
	}

	@Override
	public void apply(final CompilationImpl c) {
		c.getCfg().do_out = flag;
	}
}

class CC_SetSilent implements CompilationChange {
	private final boolean flag;

	public CC_SetSilent(final boolean aB) {
		flag = aB;
	}

	@Override
	public void apply(final CompilationImpl c) {
		c.getCfg().silent = flag;
	}
}
