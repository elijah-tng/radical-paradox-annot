package tripleo.elijah.comp;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.OS_Package;
import tripleo.elijah.lang.Qualident;
import tripleo.elijah.nextgen.outputtree.EOT_OutputTree;
import tripleo.elijah.nextgen.query.Operation2;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyComp;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.testing.comp.IFunctionMapHook;

import java.io.File;
import java.util.List;

public interface Compilation {
	void feedCmdLine(@NotNull List<String> args);

	String getProjectName();

	OS_Module realParseElijjahFile(String f, @NotNull File file, boolean do_out) throws Exception;

	Operation<CompilerInstructions> parseEzFile(@NotNull File aFile);

	void pushItem(CompilerInstructions aci);

	List<ClassStatement> findClass(String string);

	void use(@NotNull CompilerInstructions compilerInstructions, boolean do_out) throws Exception;

	int errorCount();

	IO getIO();

	void addModule(OS_Module module, String fn);

	OS_Module fileNameToModule(String fileName);

	ErrSink getErrSink();

	boolean getSilence();

	OS_Package makePackage(Qualident aPackageName);

	String getCompilationNumberString();

	@Deprecated
	int modules_size();

	@Deprecated
	@NotNull
	List<OS_Module> getModules();

	@NotNull
	EOT_OutputTree getOutputTree();

	FluffyComp getFluffy();

	static boolean isGitlab_ci() {
		return System.getenv("GITLAB_CI") != null;
	}

	static ElLog.Verbosity gitlabCIVerbosity() {
		final boolean gitlab_ci = isGitlab_ci();
		return gitlab_ci ? ElLog.Verbosity.SILENT : ElLog.Verbosity.VERBOSE;
	}

	Operation2<OS_Module> findPrelude(String aPrelude);

	List<ElLog> getElLogs();

	CompilationConfig getCfg();

	MOD getMod();

	PipelineLogic getPipelineLogic();

	void setPipelineLogic(PipelineLogic aPipelineLogic);

	void addPipeline(PipelineMember aPipelineMember);

	World world();

	Codeable codable();

	void hasInstructions(CompilerInstructions aCompilerInstructions);

	ModuleBuilder moduleBuilder();

	@Deprecated
	void testMapHooks(List<IFunctionMapHook> aMapHooks);

	interface World {
		boolean isPackage(String aString);

		OS_Package getPackage(Qualident aImportStatementItem);
	}

	interface Codeable {
		int nextFunctionCode();

		int nextClassCode();

		int nextPackageCode();
	}
}
