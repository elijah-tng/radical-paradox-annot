package tripleo.elijah.comp.queries;

import antlr.*;
import tripleo.elijah.*;
import tripleo.elijah.comp.*;
import tripleo.elijah.lang.*;
import tripleo.elijah.nextgen.query.*;
import tripleo.elijah.util.*;
import tripleo.elijjah.*;

import java.io.*;

public class QuerySourceFileToModule {
	private final QuerySourceFileToModuleParams params;
	private       CompilationImpl               compilation;

	public QuerySourceFileToModule(final QuerySourceFileToModuleParams aParams, final CompilationImpl aCompilation) {
		params      = aParams;
		compilation = aCompilation;
	}

	public OS_Module load(final QueryDatabase qb) {
		throw new NotImplementedException();
	}

	public Operation<OS_Module> calculate() {
		final String      f      = params.sourceFilename;
		final InputStream s      = params.inputStream;
		final boolean     do_out = params.do_out;

		final ElijjahLexer lexer = new ElijjahLexer(s);
		lexer.setFilename(f);
		final ElijjahParser parser = new ElijjahParser(lexer);
		parser.out = new Out(f, compilation, do_out);
		parser.setFilename(f);
		try {
			parser.program();
		} catch (RecognitionException aE) {
			return Operation.failure(aE);
		} catch (TokenStreamException aE) {
			return Operation.failure(aE);
		}
		final OS_Module module = parser.out.module();
		parser.out = null;
		return Operation.success(module);
	}


}
