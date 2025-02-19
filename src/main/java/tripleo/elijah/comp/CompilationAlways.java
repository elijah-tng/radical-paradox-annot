package tripleo.elijah.comp;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.stages.logging.ElLog;

public class CompilationAlways {
	@NotNull
	public static String defaultPrelude() {
		return "c";
	}

	public static ElLog.Verbosity gitlabCIVerbosity() {
		final boolean gitlab_ci = isGitlab_ci();
		return gitlab_ci ? ElLog.Verbosity.SILENT : ElLog.Verbosity.VERBOSE;
	}

	public static boolean isGitlab_ci() {
		return System.getenv("GITLAB_CI") != null;
	}
}
