package tripleo.elijah_fluffy.util;

public class EventualExtract {
    public static <T> T of(final Eventual<T> e) {
        // TODO ??
        // noinspection unchecked
        final T[] res = (T[]) new Object[1];
        e.then(r -> res[0] = r);
        return res[0];
    }
}
