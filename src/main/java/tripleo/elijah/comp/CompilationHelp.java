/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import com.google.common.base.Preconditions;
import org.jdeferred2.impl.DeferredObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

interface RuntimeProcess {
	void run();

	void postProcess();

	void prepare() throws Exception;
}

class StageToRuntime {
	@Contract("_, _, _ -> new")
	@NotNull
	public static RuntimeProcesses get(final @NotNull Stages stage,
	                                   final @NotNull ICompilationAccess ca,
	                                   final @NotNull ProcessRecord aPr) {
		final RuntimeProcesses r = new RuntimeProcesses(ca, aPr);

		r.add(stage.getProcess(ca, aPr));

		return r;
	}
}

class RuntimeProcesses {
	private final ICompilationAccess ca;
	private final ProcessRecord      pr;
	private       RuntimeProcess     process;

	public RuntimeProcesses(final @NotNull ICompilationAccess aca, final @NotNull ProcessRecord aPr) {
		ca = aca;
		pr = aPr;
	}

	public void add(final RuntimeProcess aProcess) {
		process = aProcess;
	}

	public int size() {
		return process == null ? 0 : 1;
	}

	public void run_better() throws Exception {
		// do nothing. job over
		if (ca.getStage() == Stages.E) return;

		// rt.prepare();
		System.err.println("***** RuntimeProcess [prepare] named " + process);
		process.prepare();

		// rt.run();
		System.err.println("***** RuntimeProcess [run    ] named " + process);
		process.run();

		// rt.postProcess(pr);
		System.err.println("***** RuntimeProcess [postProcess] named " + process);
		process.postProcess();

		System.err.println("***** RuntimeProcess^ [postProcess/writeLogs]");
		pr.writeLogs(ca);
	}
}

final class EmptyProcess implements RuntimeProcess {
	public EmptyProcess(final ICompilationAccess aCompilationAccess, final ProcessRecord aPr) {
	}

	@Override
	public void postProcess() {
	}

	@Override
	public void run() {
	}

	@Override
	public void prepare() {
	}
}

class DStageProcess implements RuntimeProcess {
	private final ICompilationAccess ca;
	private final ProcessRecord      pr;

	@Contract(pure = true)
	public DStageProcess(final ICompilationAccess aCa, final ProcessRecord aPr) {
		ca = aCa;
		pr = aPr;
	}

	@Override
	public void run() {
		final int y = 2;
	}

	@Override
	public void postProcess() {
	}

	@Override
	public void prepare() {
		assert ca.getStage() == Stages.D;
	}
}

class OStageProcess implements RuntimeProcess {
	private final ProcessRecord      pr;
	private final ICompilationAccess ca;

	private final DeferredObject<PipelineLogic, Void, Void> ppl = new DeferredObject<>();

	OStageProcess(final ICompilationAccess aCa, final ProcessRecord aPr) {
		ca = aCa;
		pr = aPr;
	}

	@Override
	public void run() {
		final Compilation comp = ca.getCompilation();

		ppl.then((pl) -> {
			final Pipeline ps = comp.processModel().getPipelines();

			try {
				ps.run();
			} catch (final Exception ex) {
//				Logger.getLogger(OStageProcess.class.getName()).log(Level.SEVERE, "Error during Piplines#run from OStageProcess", ex);
				comp.getErrSink().exception(ex);
			}

			comp.moveMe().writeLogs(comp.getCfg().silent, comp.getElLogs());
		});
	}

	@Override
	public void postProcess() {
	}

	@Override
	public void prepare() throws Exception {
		Preconditions.checkNotNull(pr);
		Preconditions.checkNotNull(pr.pipelineLogic);
		Preconditions.checkNotNull(pr.ab.gr);

		ppl.resolve(pr.pipelineLogic);

		final AccessBus ab = pr.ab;

//		ab.add(DeducePipeline::new);
		ab.add(GeneratePipeline::new);
		ab.add(WritePipeline::new);
		ab.add(WriteMesonPipeline::new);

//		final DeducePipeline     dpl  = new DeducePipeline(ab);
//		final GeneratePipeline   gpl  = new GeneratePipeline(ab);
//		final WritePipeline      wpl  = new WritePipeline(ab);
//		final WriteMesonPipeline wmpl = new WriteMesonPipeline(comp, pr, ppl, wpl);
//
//		List_of(dpl, gpl, wpl, wmpl)
//		  .forEach(ca::addPipeline);

		ppl.then(pl -> {
			final Compilation comp = ca.getCompilation();

			comp.getMod().modules.stream().forEach(pl::addModule);
		});
	}
}

//
//
//
