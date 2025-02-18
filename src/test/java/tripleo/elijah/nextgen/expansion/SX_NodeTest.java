package tripleo.elijah.nextgen.expansion;

import junit.framework.TestCase;
import tripleo.elijah.comp.*;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.nextgen.model.*;
import tripleo.elijah.stages.gen_c.GenerateC;
import tripleo.elijah.stages.gen_generic.GenerateFiles;
import tripleo.elijah.stages.gen_generic.OutputFileFactoryParams;
import tripleo.elijah.stages.logging.ElLog;

import java.util.List;

import static tripleo.elijah.util.Helpers.List_of;

public class SX_NodeTest extends TestCase {

	public void testFullText() {
		final StdErrSink      errSink       = new StdErrSink();
		final IO              io            = new IO();
		final Compilation comp          = tripleo.elijah.factory.comp.CompilationFactory.mkCompilation(errSink, io);
		final AccessBus       ab            = new AccessBus(comp);
		final PipelineLogic   pipelineLogic = new PipelineLogic(ab);
		final OS_Module mod = comp.moduleBuilder()
		                          .withFileName("filename.elijah")
		                          .addToCompilation()
		                          .build();
		final OutputFileFactoryParams p    = new OutputFileFactoryParams(mod, errSink, ElLog.Verbosity.SILENT, pipelineLogic);
		final GenerateFiles           fgen = new GenerateC(p);

		final SM_ClassDeclaration node = new SM_ClassDeclaration() {
			@Override
			public SM_Name name() {
				return new SM_Name() {
					@Override
					public String getText() {
						return "Main";
					}
				};
			}

			@Override
			public SM_ClassSubtype subType() {
				return SM_ClassSubtype.NORMAL;
			}

			@Override
			public SM_ClassInheritance inheritance() {
				return new SM_ClassInheritance() {
					@Override
					public List<SM_Name> names() {
						return List_of(new SM_Name() {
							@Override
							public String getText() {
								return "Arguments";
							}
						});
					}
				};
			}

			@Override
			public SM_ClassBody classBody() {
				return null;
			}
		};

		fgen.forNode(node);
	}
}