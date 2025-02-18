package tripleo.elijah.comp;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.OS_Module;

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
}
