package tripleo.elijah.comp.queries;

import tripleo.elijah.Out;
import tripleo.elijah_fluffy.util.Eventual;

import java.io.InputStream;

public class QuerySourceFileToModuleParams {
	public final InputStream inputStream;
	public final String      sourceFilename;
	public final boolean     do_out;

	private final Eventual<Out.OS_ModuleX> eventual = new Eventual<>();

	public QuerySourceFileToModuleParams(final InputStream aInputStream, final String aSourceFilename, final boolean aDo_out) {
		inputStream    = aInputStream;
		sourceFilename = aSourceFilename;
		do_out         = aDo_out;
	}

	public Eventual<Out.OS_ModuleX> getEventual() {
		return eventual;
	}
}
