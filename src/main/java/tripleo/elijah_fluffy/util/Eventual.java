package tripleo.elijah_fluffy.util;

import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.diagnostic.ExceptionDiagnostic;
import tripleo.elijah.diagnostic.Diagnostic;

public class Eventual<P> {
    private final DeferredObject<P, Diagnostic, Void> prom = new DeferredObject<>();

    public void resolve(final P p) {
        prom.resolve(p);
    }

    public void then(final DoneCallback<? super P> cb) {
        prom.then(cb);
    }

    public void register(final @NotNull EventualRegister ev) {
        ev.register(this);
    }

    public void fail(final Diagnostic d) {
        prom.reject(d);
    }

    public boolean isResolved() {
        return prom.isResolved();
    }

    /**
     * Please overload this
     */
    public String description() {
        return "GENERIC-DESCRIPTION";
    }

    public boolean isFailed() {
        return prom.isRejected();
    }

    public boolean isPending() {
        return prom.isPending();
    }

    public void reject(final Diagnostic aDiagnostic) {
        prom.reject(aDiagnostic);
    }

    public Promise.State state() {
        return prom.state();
    }

    @Deprecated
    public void done(final DoneCallback<P> cb) {
        then(cb);
    }

    public void onFail(final FailCallback<Diagnostic> fc) {
        prom.fail(fc);
    }

    public void fail(final Exception aE) {
        fail(new ExceptionDiagnostic(aE));
    }

    public void reject(final Throwable aFailure) {
        prom.reject(new ExceptionDiagnostic(aFailure));
    }
}
