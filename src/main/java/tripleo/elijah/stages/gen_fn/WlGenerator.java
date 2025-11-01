package tripleo.elijah.stages.gen_fn;

import tripleo.elijah.comp.Compilation;
import tripleo.elijah_fluffy.util.Eventual;

public interface WlGenerator<T> {
    Compilation.Codeable getCodable();

    void advise(Compilation aCompilation);

    Eventual<T> generated();
}
