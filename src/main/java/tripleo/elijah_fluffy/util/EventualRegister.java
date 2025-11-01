package tripleo.elijah_fluffy.util;

public interface EventualRegister {
    <P> void register(Eventual<P> e);

    void checkFinishEventuals();
}
