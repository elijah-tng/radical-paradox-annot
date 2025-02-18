package tripleo.elijah.comp;

import com.google.common.collect.*;
import io.reactivex.rxjava3.functions.*;
import org.jdeferred2.*;
import org.jetbrains.annotations.*;
import tripleo.elijah.comp.functionality.f202.*;
import tripleo.elijah.stages.deduce.*;
import tripleo.elijah.stages.gen_fn.*;
import tripleo.elijah.stages.logging.*;

import java.util.*;

class DefaultCompilationAccess implements ICompilationAccess {
	protected final CompilationImpl                            compilation;
	private final   DeferredObject2<PipelineLogic, Void, Void> pipelineLogicDeferred = new DeferredObject2<>();

	public DefaultCompilationAccess(final CompilationImpl aCompilation) {
		compilation = aCompilation;
	}

	void registerPipelineLogic(final Consumer<PipelineLogic> aPipelineLogicConsumer) {
		pipelineLogicDeferred.then(new DoneCallback<PipelineLogic>() {
			@Override
			public void onDone(final PipelineLogic result) {
				try {
					aPipelineLogicConsumer.accept(result);
				} catch (final Throwable aE) {
					throw new RuntimeException(aE);
				}
			}
		});
	}

	@Override
	public void setPipelineLogic(final PipelineLogic pl) {
		compilation.setPipelineLogic(pl);

		pipelineLogicDeferred.resolve(pl);
	}

/*
	@Override
	public void addPipeline(final PipelineMember pl) {
		compilation.addPipeline(pl);
	}
*/

	@Override
	@NotNull
	public ElLog.Verbosity testSilence() {
		//final boolean isSilent = compilation.silent; // TODO No such thing. silent is a local var
		final boolean isSilent = false; // TODO fix this

		return isSilent ? ElLog.Verbosity.SILENT : ElLog.Verbosity.VERBOSE;
	}

	@Override
	public CompilationImpl getCompilation() {
		return compilation;
	}

	@Override
	public void writeLogs() {
		final boolean silent = testSilence() == ElLog.Verbosity.SILENT;

		writeLogs(silent, compilation.getElLogs());
	}

	@Override
	public List<FunctionMapHook> functionMapHooks() {
		return null;
	}

//	@Override
//	public Pipeline pipelines() {
//		return compilation.getPipelines();
//	}

	@Override
	public Stages getStage() {
		return getCompilation().getCfg().stage;
	}

	private void writeLogs(final boolean aSilent, final List<ElLog> aLogs) {
		final Multimap<String, ElLog> logMap = ArrayListMultimap.create();
		if (true) {
			for (final ElLog deduceLog : aLogs) {
				logMap.put(deduceLog.getFileName(), deduceLog);
			}
			for (final Map.Entry<String, Collection<ElLog>> stringCollectionEntry : logMap.asMap().entrySet()) {
				final F202 f202 = new F202(compilation.getErrSink(), compilation);
				f202.processLogs(stringCollectionEntry.getValue());
			}
		}
	}
}
