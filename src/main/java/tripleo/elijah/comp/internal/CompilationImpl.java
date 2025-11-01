/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.comp.*;
import tripleo.elijah.comp.functionality.f202.F202;
import tripleo.elijah.comp.queries.QueryEzFileToModule;
import tripleo.elijah.comp.queries.QueryEzFileToModuleParams;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.OS_Package;
import tripleo.elijah.lang.Qualident;
import tripleo.elijah.nextgen.outputtree.EOT_OutputTree;
import tripleo.elijah.nextgen.query.Operation2;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.FunctionMapHook;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyComp;
import tripleo.elijah.stages.deduce.fluffy.impl.FluffyCompImpl;
import tripleo.elijah.stages.gen_fn.GeneratedNode;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.testing.comp.IFunctionMapHook;
import tripleo.elijah.util.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static tripleo.elijah.util.Helpers.List_of;

public class CompilationImpl implements Compilation {
    private final          List<ElLog>             elLogs       = new LinkedList<>();
    private final          Map<String, OS_Package> _packages    = new HashMap<>();
    private final          CompilationConfig       cfg          = new CompilationConfig();
    private final          MOD                     mod          = new MOD(this);
    private final          CIS                     _cis         = new CIS();
    private final          USE                     use          = new USE(this);
    private @NotNull       EOT_OutputTree          _output_tree = new EOT_OutputTree();
    private final          Pipeline                pipelines;
    private final          int                     _compilationNumber;
    private final          ErrSink                 errSink;
    private final          IO                      io;
    private final @NotNull FluffyCompImpl          _fluffyComp;
    private final          Codeable                _codeable;
    private                PipelineLogic           pipelineLogic;
    private                CompilationRunner       __cr;
    private                CompilerInstructions    rootCI;
	private final RpProcessModel _processModel;
	private final MoveMe _moveMe;

	public CompilationImpl(final ErrSink aErrSink, final IO aIo) {
        errSink            = aErrSink;
        io                 = aIo;
        _compilationNumber = new Random().nextInt(Integer.MAX_VALUE);
        pipelines          = new Pipeline(aErrSink);
        _fluffyComp        = new FluffyCompImpl(this);

        final var _c = this;
        _codeable = new Codeable() {
            private int _packageCode  = 1;
            private int _classCode    = 101;
            private int _functionCode = 1001;

            @Override
            public int nextClassCode() {
                return _classCode++;
            }

            @Override
            public int nextFunctionCode() {
                return _functionCode++;
            }

            @Override
            public int nextPackageCode() {
                return _packageCode++;
            }
        };
				_processModel = new RpProcessModel() {
					@Override
					public Pipeline getPipelines() {
						return _c.pipelines;
					}
				};
				_moveMe = new MoveMe() {
					@Override
					public void writeLogs(final boolean aSilent, final List<ElLog> aElLogs) {
						final Multimap<String, ElLog> logMap = ArrayListMultimap.create();
						if (RpFeatureFlags.Compilation_writeLogs) {
								for (final ElLog deduceLog : aElLogs) {
										logMap.put(deduceLog.getFileName(), deduceLog);
								}
								for (final Map.Entry<String, Collection<ElLog>> stringCollectionEntry : logMap.asMap().entrySet()) {
										final F202 f202 = new F202(_c.getErrSink(), _c);
										f202.processLogs(stringCollectionEntry.getValue());
								}
						}
					}

					@Override
					public void addFunctionMapHook(final FunctionMapHook aAddFunctionMapHook) {
						_c.getDeducePhase().addFunctionMapHook(aAddFunctionMapHook);
					}
				};
	}

	public enum RpFeatureFlags {
		;

		public static final boolean Compilation_writeLogs = true;
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
            final OS_Package newPackage = new OS_Package(pkg_name, codable().nextPackageCode());
            _packages.put(pkg_name.toString(), newPackage);
            return newPackage;
        } else
            return _packages.get(pkg_name.toString());
    }

    // endregion

    // public int compilationNumber() {
    //     return _compilationNumber;
    // }

    @Override
    public String getCompilationNumberString() {
        return String.format("%08x", _compilationNumber);
    }

    @Override
    @Deprecated
    public int modules_size() {
        return mod.size();
    }

    @Override
    @Deprecated
    public @NotNull List<OS_Module> getModules() {
        return mod.modules();
    }

    public @NotNull List<GeneratedNode> getLGC() {
        return getDeducePhase().generatedClasses.copy();
    }

	public ModuleBuilder moduleBuilder() {
        return new ModuleBuilder(this);
    }

    public void testMapHooks(final List<IFunctionMapHook> aMapHooks) {
        throw new NotImplementedException();
    }

	@Override
	public RpProcessModel processModel() {
		return this._processModel;
	}

	@Override
	public MoveMe moveMe() {
		return this._moveMe;
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

    @Override
    public List<ElLog> getElLogs() {
        return elLogs;
    }

    @Override
    public CompilationConfig getCfg() {
        return cfg;
    }

    @Override
    public MOD getMod() {
        return mod;
    }

    @Override
    public PipelineLogic getPipelineLogic() {
        return pipelineLogic;
    }

    @Override
    public void setPipelineLogic(PipelineLogic aPipelineLogic) {
        pipelineLogic = aPipelineLogic;
    }

    @Override
    public void addPipeline(final PipelineMember aPipelineMember) {
        pipelines.add(aPipelineMember);
    }

    @Override
    public World world() {
        final CompilationImpl _c = this;
        return new World() {
            @Override
            public boolean isPackage(final String aString) {
                return _c.isPackage(aString);
            }

            @Override
            public OS_Package getPackage(final Qualident aPackageName) {
                return _c.getPackage(aPackageName);
            }
        };
    }

    @Override
    public Codeable codable() {
        return this._codeable;
    }

    @Override
    public void hasInstructions(final CompilerInstructions aCompilerInstructions) {
        final CompilationImpl _c = this;
        try {
            _c.hasInstructions(List_of(aCompilerInstructions));
        } catch (Exception aE) {
            throw new RuntimeException(aE);
        }
    }
}

//
//
//
