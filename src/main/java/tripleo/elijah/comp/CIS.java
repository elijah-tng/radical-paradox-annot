package tripleo.elijah.comp;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import tripleo.elijah.ci.CompilerInstructions;

public class CIS implements Observer<CompilerInstructions> {
    private final Subject<CompilerInstructions> compilerInstructionsSubject = ReplaySubject.create();
    CompilerInstructionsObserver _cio;

    @Override
    public void onSubscribe(@NonNull final Disposable d) {
        compilerInstructionsSubject.onSubscribe(d);
    }

    @Override
    public void onNext(@NonNull final CompilerInstructions aCompilerInstructions) {
        compilerInstructionsSubject.onNext(aCompilerInstructions);
    }

    @Override
    public void onError(@NonNull final Throwable e) {
        compilerInstructionsSubject.onError(e);
    }

    @Override
    public void onComplete() {
        throw new IllegalStateException();
        // compilerInstructionsSubject.onComplete();
    }

    public void almostComplete() {
        _cio.almostComplete();
    }

    public void subscribe(final Observer<CompilerInstructions> aCio) {
        compilerInstructionsSubject.subscribe(aCio);
    }
}
