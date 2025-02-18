package tripleo.elijah.stages.deduce.fluffy.impl;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.entrypoints.MainClassEntryPoint;
import tripleo.elijah.lang.*;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyComp;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyModule;

import java.util.*;
import java.util.stream.Collectors;

public class FluffyCompImpl implements FluffyComp {
	private final Compilation              _comp;
	private final Map<OS_Module, FluffyModule> fluffyModuleMap = new HashMap<>();

	public FluffyCompImpl(final Compilation aComp) {
		_comp = aComp;
	}

	public static boolean isMainClassEntryPoint(@NotNull final ClassItem input) {
		final FunctionDef fd = (FunctionDef) input;
		return MainClassEntryPoint.is_main_function_with_no_args(fd);
	}

	@Override
	public FluffyModule module(final OS_Module aModule) {
		if (fluffyModuleMap.containsKey(aModule)) {
			return fluffyModuleMap.get(aModule);
		}

		final FluffyModuleImpl fluffyModule = new FluffyModuleImpl(aModule, _comp);

		fluffyModuleMap.put(aModule, fluffyModule);
//		fluffyModule.

		return fluffyModule;
	}
}
