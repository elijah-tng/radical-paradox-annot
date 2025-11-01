package tripleo.elijah.comp.queries;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.Out;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.Operation;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.nextgen.query.QueryDatabase;
import tripleo.elijah.util.Helpers;
import tripleo.elijah_fluffy.util.EventualExtract;
import tripleo.elijjah.ElijjahLexer;
import tripleo.elijjah.ElijjahParser;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class QuerySourceFileToModule {
	private final QuerySourceFileToModuleParams params;
	private final Compilation                   compilation;
	private boolean loaded;
	private String hash;

	public QuerySourceFileToModule(final QuerySourceFileToModuleParams aParams, final Compilation aCompilation) {
		params      = aParams;
		compilation = aCompilation;
	}

	public OS_Module load(final QueryDatabase qb) {
		this.loaded = true;
		assert params.getEventual().isResolved();
		return EventualExtract.of(params.getEventual()).module();
	}

	public Operation<OS_Module> calculate() {
		final String      f      = params.sourceFilename;
		final InputStream s      = params.inputStream;
		final boolean     do_out = params.do_out;

		final ElijjahLexer lexer = new ElijjahLexer(s);
		lexer.setFilename(f);
		final ElijjahParser parser = new ElijjahParser(lexer);
		parser.out = new Out(f, compilation, do_out, params.getEventual());
		final Out out = parser.out;
		parser.setFilename(f);
		try {
			parser.program(out.closure(), out.module());
		} catch (RecognitionException aE) {
			return Operation.failure(aE);
		} catch (TokenStreamException aE) {
			return Operation.failure(aE);
		}
		final OS_Module module = parser.out.module();
		parser.out = null;
		return Operation.success(module);
	}

	public String getHash() {
		return this.hash;
	}

	public boolean resultIsPresent() {
		final QueryDatabase db = this.compilation.queryDb();

		final String hash = hashOf(params.sourceFilename);
		this.hash = hash;
		if(db.containsKey(hash)) {
			return true;
		}

		return false;
	}

	// returns the hash of the filename
	private @NotNull String hashOf(final @NotNull String aSourceFilename) {
		try {
			final String moduleSha = Helpers.getHash(aSourceFilename.getBytes());
			return "/module/sha/"+moduleSha;
		} catch (NoSuchAlgorithmException aE) {
			// try {
			// 	return "/module/uuid/"+ Helpers.getHash(UUID.randomUUID().toString().getBytes());
			// } catch (NoSuchAlgorithmException aEx) {
			// 	throw new RuntimeException(aEx);
			// }
		}
	}
}
