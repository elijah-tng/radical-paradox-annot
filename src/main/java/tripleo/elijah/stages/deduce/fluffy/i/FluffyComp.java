package tripleo.elijah.stages.deduce.fluffy.i;

import tripleo.elijah_fluffy.util.EventualRegister;

public interface FluffyComp extends EventualRegister {
	public void addModuleFromSource(FluffyModuleSource aFluffyModuleSource);
}
