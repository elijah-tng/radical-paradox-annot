package tripleo.elijah.comp;

import org.jetbrains.annotations.*;
import tripleo.elijah.ci.*;
import tripleo.elijah.stages.deduce.post_bytecode.*;

class CCI {
	//private final @NotNull Compilation compilation;
	private final CIS _cis;

	@Contract(pure = true)
	CCI(final @NotNull Compilation aCompilation, final CIS aCis) {
		//compilation = aCompilation;
		_cis = aCis;
	}

	public void accept(final @NotNull Maybe<ILazyCompilerInstructions> mcci) {
		if (mcci.isException()) return;

		final ILazyCompilerInstructions cci = mcci.o;
		final CompilerInstructions      ci  = cci.get();

		System.err.println("*** " + ci.getName());

		_cis.onNext(ci);
		//compilation.pushItem(ci);
	}
}
