package tripleo.elijah.stages.deduce.fluffy.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.entrypoints.MainClassEntryPoint;
import tripleo.elijah.lang.*;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyComp;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyModule;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyModuleSource;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.UnintendedUseException;

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

	public FluffyModule module(final OS_Module aModule) {
		if (fluffyModuleMap.containsKey(aModule)) {
			return fluffyModuleMap.get(aModule);
		}

		final FluffyModuleImpl fluffyModule = new FluffyModuleImpl(aModule, _comp);

		fluffyModuleMap.put(aModule, fluffyModule);
//		fluffyModule.

		return fluffyModule;
	}

    @Override
    public <P> void register(Eventual<P> e) {

    }

    @Override
    public void checkFinishEventuals() {

    }

	public void find_multiple_items(final OS_Module aModule) {
		final Multimap<String, ModuleItem> items_map = ArrayListMultimap.create(aModule.getItems().size(), 1);
		for (final ModuleItem item : aModule.getItems()) {
			if (!(item instanceof OS_Element2/* && item != anElement*/))
				continue;
			final String item_name = ((OS_Element2) item).name();
			items_map.put(item_name, item);
		}
		for (final String key : items_map.keys()) {
			boolean warn = false;

			final Collection<ModuleItem> moduleItems = items_map.get(key);
			if (moduleItems.size() < 2) // README really 1
				continue;

			final Collection<ElObjectType> t = moduleItems
			  .stream()
			  .map((final ModuleItem input) -> DecideElObjectType.getElObjectType(input))
			  .collect(Collectors.toList());

			final Set<ElObjectType> st = new HashSet<ElObjectType>(t);
			if (st.size() > 1)
				warn = true;
			if (moduleItems.size() > 1)
				if (moduleItems.iterator().next() instanceof NamespaceStatement && st.size() == 1)
					;
				else
					warn = true;

			//
			//
			//

			if (warn) {
				final String module_name = aModule.toString(); // TODO print module name or something
				final String s = String.format(
						"[Module#add] %s Already has a member by the name of %s",
						module_name, key);
				aModule.getCompilation().getErrSink().reportWarning(s);
			}
		}
	}

	@Override
	public void addModuleFromSource(final FluffyModuleSource aFluffyModuleSource) {
		throw new UnintendedUseException();
	}
}
