package tripleo.elijah.stages.deduce.fluffy.impl;

import tripleo.elijah.nextgen.query.QueryRef;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyModuleSource;

public class FluffyModuleSourceQueryRef implements FluffyModuleSource {
	private final QueryRef queryRef;

	public FluffyModuleSourceQueryRef(QueryRef aQueryRef) {
		this.queryRef = aQueryRef;
	}
}
