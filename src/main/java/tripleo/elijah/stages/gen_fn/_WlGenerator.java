package tripleo.elijah.stages.gen_fn;

import tripleo.elijah.comp.Compilation;
import tripleo.elijah_fluffy.util.Eventual;

public abstract class _WlGenerator<T> implements WlGenerator<T> {
    private Eventual<T> ev = new Eventual<>();

    @Override
    public void advise(Compilation aCompilation) {
        ev.register(aCompilation.getFluffy());
    }

    @Override
    public Eventual<T> generated() {
        return this.ev;
    }
}
