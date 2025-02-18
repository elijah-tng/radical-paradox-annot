package tripleo.elijah.comp;

import tripleo.elijah.stages.deduce.*;
import tripleo.elijah.stages.logging.*;

import java.util.*;

public interface ICompilationAccess {
	void setPipelineLogic(final PipelineLogic pl);

//	void addPipeline(final PipelineMember pl);

	ElLog.Verbosity testSilence();

	CompilationImpl getCompilation();

	void writeLogs();

	List<FunctionMapHook> functionMapHooks();

//	Pipeline pipelines();

	Stages getStage();
}
