/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import com.google.common.collect.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.ci.*;
import tripleo.elijah.comp.functionality.f202.*;
import tripleo.elijah.comp.queries.*;
import tripleo.elijah.lang.*;
import tripleo.elijah.nextgen.outputtree.*;
import tripleo.elijah.nextgen.query.*;
import tripleo.elijah.stages.deduce.*;
import tripleo.elijah.stages.deduce.fluffy.i.*;
import tripleo.elijah.stages.deduce.fluffy.impl.FluffyCompImpl;
import tripleo.elijah.stages.gen_fn.*;
import tripleo.elijah.stages.logging.*;
import tripleo.elijah.testing.comp.IFunctionMapHook;
import tripleo.elijah.util.NotImplementedException;

import java.io.*;
import java.util.*;

public class CompilationImpl implements Compilation {
    private final List<ElLog>             elLogs    = new LinkedList<ElLog>();
    private final Map<String, OS_Package> _packages = new HashMap<String, OS_Package>();
    private final CompilationConfig       cfg       = new CompilationConfig();
    private final MOD                     mod       = new MOD(this);
    private final CIS                     _cis      = new CIS();
    private final          USE                     use           = new USE(this);
    private @Nullable      EOT_OutputTree          _output_tree  = null;
    private final          Pipeline                pipelines;
    private final          int                     _compilationNumber;
    private final          ErrSink                 errSink;
    private final IO                      io;
    private       PipelineLogic           pipelineLogic;
    private       CompilationRunner       __cr;
    private                CompilerInstructions    rootCI;
    private final @NotNull FluffyCompImpl          _fluffyComp;
    private                int                     _packageCode  = 1;
    private                int                     _classCode    = 101;
    private                int                     _functionCode = 1001;

    public CompilationImpl(final ErrSink aErrSink, final IO aIo) {
        errSink            = aErrSink;
        io                 = aIo;
        _compilationNumber = new Random().nextInt(Integer.MAX_VALUE);
        pipelines          = new Pipeline(aErrSink);
        _fluffyComp        = new FluffyCompImpl(this);
    }

    void hasInstructions(final @NotNull List<CompilerInstructions> cis) throws Exception {
        assert cis.size() > 0;

        rootCI = cis.get(0);

        __cr.start(rootCI, cfg.do_out);
    }

    @Override
    public void feedCmdLine(final @NotNull List<String> args) {
        if (args.size() == 0) {
            System.err.println("Usage: eljc [--showtree] [-sE|O] <directory or .ez file names>");
            return; // ab
        }

        try {
            final OptionsProcessor             op  = new ApacheOptionsProcessor();
            final CompilerInstructionsObserver cio = new CompilerInstructionsObserver(this, op, _cis);

            final String[] args2;
            args2 = op.process(this, args);

            __cr = new CompilationRunner(this, _cis);
            __cr.doFindCIs(args2);
        } catch (final Exception e) {
            errSink.exception(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getProjectName() {
        return rootCI.getName();
    }

    @Override
    public OS_Module realParseElijjahFile(final String f, final @NotNull File file, final boolean do_out) throws Exception {
        return use.realParseElijjahFile(f, file, do_out).success();
    }

    @Override
    public Operation<CompilerInstructions> parseEzFile(final @NotNull File aFile) {
        try {
            final QueryEzFileToModuleParams       params = new QueryEzFileToModuleParams(aFile.getAbsolutePath(), io.readFile(aFile));
            final Operation<CompilerInstructions> x      = new QueryEzFileToModule(params).calculate();
            return x;
        } catch (final FileNotFoundException aE) {
            return Operation.failure(aE);
        }
    }

    //
    //
    //

    @Override
    public void pushItem(final CompilerInstructions aci) {
        _cis.onNext(aci);
    }

    @Override
    public List<ClassStatement> findClass(final String string) {
        final List<ClassStatement> l = new ArrayList<ClassStatement>();
        for (final OS_Module module : mod.modules) {
            if (module.hasClass(string)) {
                l.add((ClassStatement) module.findClass(string));
            }
        }
        return l;
    }

    @Override
    public void use(final @NotNull CompilerInstructions compilerInstructions, final boolean do_out) throws Exception {
        use.use(compilerInstructions, do_out);    // NOTE Rust
    }

    @Override
    public int errorCount() {
        return errSink.errorCount();
    }

    void writeLogs(final boolean aSilent, final @NotNull List<ElLog> aLogs) {
        final Multimap<String, ElLog> logMap = ArrayListMultimap.create();
        if (true) {
            for (final ElLog deduceLog : aLogs) {
                logMap.put(deduceLog.getFileName(), deduceLog);
            }
            for (final Map.Entry<String, Collection<ElLog>> stringCollectionEntry : logMap.asMap().entrySet()) {
                final F202 f202 = new F202(getErrSink(), this);
                f202.processLogs(stringCollectionEntry.getValue());
            }
        }
    }

    @Override
    public IO getIO() {
        return io;
    }

//	public void setIO(final IO io) {
//		this.io = io;
//	}

    @Override
    public void addModule(final OS_Module module, final String fn) {
        mod.addModule(module, fn);
    }

    @Override
    public OS_Module fileNameToModule(final String fileName) {
        if (mod.fn2m.containsKey(fileName)) {
            return mod.fn2m.get(fileName);
        }
        return null;
    }

    //
    // region MODULE STUFF
    //

    @Override
    public ErrSink getErrSink() {
        return errSink;
    }

    @Override
    public boolean getSilence() {
        return cfg.silent;
    }

    // endregion

    //
    // region CLASS AND FUNCTION CODES
    //

    public Operation2<OS_Module> findPrelude(final String prelude_name) {
        return use.findPrelude(prelude_name);
    }

    public void addFunctionMapHook(final FunctionMapHook aFunctionMapHook) {
        getDeducePhase().addFunctionMapHook(aFunctionMapHook);
    }

    public @NotNull DeducePhase getDeducePhase() {
        // TODO subscribeDeducePhase??
        return pipelineLogic.dp;
    }

    public int nextClassCode() {
        return _classCode++;
    }

    public int nextFunctionCode() {
        return _functionCode++;
    }

    // endregion

    //
    // region PACKAGES
    //

    public boolean isPackage(final String pkg) {
        return _packages.containsKey(pkg);
    }

    public OS_Package getPackage(final Qualident pkg_name) {
        return _packages.get(pkg_name.toString());
    }

    public OS_Package makePackage(final Qualident pkg_name) {
        if (!isPackage(pkg_name.toString())) {
            final OS_Package newPackage = new OS_Package(pkg_name, nextPackageCode());
            _packages.put(pkg_name.toString(), newPackage);
            return newPackage;
        } else
            return _packages.get(pkg_name.toString());
    }

    private int nextPackageCode() {
        return _packageCode++;
    }

    // endregion

    public int compilationNumber() {
        return _compilationNumber;
    }

    public String getCompilationNumberString() {
        return String.format("%08x", _compilationNumber);
    }

    @Deprecated
    public int modules_size() {
        return mod.size();
    }

    @Deprecated
    public @NotNull List<OS_Module> getModules() {
        return mod.modules();
    }

    public @NotNull List<GeneratedNode> getLGC() {
        return getDeducePhase().generatedClasses.copy();
    }

    public Pipeline getPipelines() {
        return pipelines;
    }

    public ModuleBuilder moduleBuilder() {
        return new ModuleBuilder(this);
    }

    public void testMapHooks(final List<IFunctionMapHook> aMapHooks) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull EOT_OutputTree getOutputTree() {
        if (_output_tree == null) {
            _output_tree = new EOT_OutputTree();
        }

        assert _output_tree != null;

        return _output_tree;
    }

    @Override
    public @NotNull FluffyComp getFluffy() {
        return _fluffyComp;
    }

    public static boolean isGitlab_ci() {
        return System.getenv("GITLAB_CI") != null;
    }

    public static ElLog.Verbosity gitlabCIVerbosity() {
        final boolean gitlab_ci = isGitlab_ci();
        return gitlab_ci ? ElLog.Verbosity.SILENT : ElLog.Verbosity.VERBOSE;
    }

    public List<ElLog> getElLogs() {
        return elLogs;
    }

    public CompilationConfig getCfg() {
        return cfg;
    }

    public MOD getMod() {
        return mod;
    }

    public PipelineLogic getPipelineLogic() {
        return pipelineLogic;
    }

    public void setPipelineLogic(PipelineLogic aPipelineLogic) {
        pipelineLogic = aPipelineLogic;
    }
}

//
//
//
