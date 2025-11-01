package tripleo.elijah.nextgen.inputtree;

import org.jetbrains.annotations.*;
import tripleo.elijah.ci.*;
import tripleo.elijah.comp.*;
import tripleo.elijah.lang.*;
import tripleo.elijah.stages.gen_fn.*;
import tripleo.elijah.stages.gen_generic.*;
import tripleo.elijah.stages.logging.*;
import tripleo.elijah.work.*;

import java.util.*;
import java.util.function.*;

public class EIT_ModuleInput implements EIT_Input {
    private final OS_Module module;
    private final Compilation c;

    @Contract(pure = true)
    public EIT_ModuleInput(final OS_Module module, final Compilation c) {
        this.module = module;
        this.c      = c;
    }

    @Override
    public EIT_InputType getType() {
        return EIT_InputType.ELIJAH_SOURCE;
    }

    public void doGenerate(final List<GeneratedNode> nodes,
                           final ErrSink aErrSink,
                           final ElLog.Verbosity verbosity,
                           final PipelineLogic pipelineLogic,
                           final WorkManager wm,
                           final @NotNull Consumer<GenerateResult> resultConsumer) {
        // 0. get lang
        final String lang = langOfModule();

        // 1. find Generator (GenerateFiles) eg. GenerateC
        final OutputFileFactoryParams p             = new OutputFileFactoryParams(module, aErrSink, verbosity, pipelineLogic);
        final GenerateFiles           generateFiles = OutputFileFactory.create(lang, p);

        // 2. query results
        final GenerateResult gr2 = generateFiles.resultsFromNodes(nodes, wm);

        // 3. #drain workManager -> README part of workflow. may change later as appropriate
        wm.drain();

        // 4. tail process results
        resultConsumer.accept(gr2);
    }

    @NotNull
    private String langOfModule() {
        final LibraryStatementPart lsp  = module.getLsp();
        final CompilerInstructions ci   = lsp.getInstructions();
        final String               lang = ci.genLang() == null ? CompilationAlways.defaultPrelude() : ci.genLang();
        // DEFAULT(compiler-default), SPECIFIED(gen-clause: codePoint), INHERITED(cp) // CodePoint??
        return lang;
    }
}
