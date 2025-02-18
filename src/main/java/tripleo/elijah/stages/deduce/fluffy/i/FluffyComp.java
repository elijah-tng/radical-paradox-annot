package tripleo.elijah.stages.deduce.fluffy.i;

import tripleo.elijah.lang.OS_Module;
import tripleo.elijah_fluffy.util.EventualRegister;

public interface FluffyComp extends EventualRegister {
	public void addModuleFromSource(FluffyModuleSource aFluffyModuleSource);

	FluffyModule module(OS_Module aOSModule);

	void find_multiple_items(OS_Module aModule);
}
