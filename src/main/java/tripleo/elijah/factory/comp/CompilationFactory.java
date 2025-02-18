package tripleo.elijah.factory.comp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.ErrSink;
import tripleo.elijah.comp.IO;
import tripleo.elijah.comp.StdErrSink;
import tripleo.elijah.testing.comp.IFunctionMapHook;

import java.util.List;

public class CompilationFactory {

	public static Compilation mkCompilation2(final List<IFunctionMapHook> aMapHooks) {
		final StdErrSink errSink = new StdErrSink();
		final IO         io      = new IO();

		final @NotNull Compilation c = mkCompilation(errSink, io);

		c.testMapHooks(aMapHooks);

		return c;
	}

	@Contract("_, _ -> new")
	public static @NotNull Compilation mkCompilation(final ErrSink eee, final IO io) {
		return new Compilation(eee, io);
	}
}
