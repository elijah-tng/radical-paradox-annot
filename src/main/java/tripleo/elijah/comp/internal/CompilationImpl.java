/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.*;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.comp.*;
import tripleo.elijah.comp.functionality.f202.F202;
import tripleo.elijah.comp.queries.QueryEzFileToModule;
import tripleo.elijah.comp.queries.QueryEzFileToModuleParams;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.OS_Package;
import tripleo.elijah.lang.Qualident;
import tripleo.elijah.nextgen.outputtree.*;
import tripleo.elijah.nextgen.query.Operation2;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.FunctionMapHook;
import tripleo.elijah.stages.deduce.fluffy.i.*;
import tripleo.elijah.stages.deduce.fluffy.impl.*;
import tripleo.elijah.stages.gen_fn.GeneratedNode;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.testing.comp.*;
import tripleo.elijah.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

// public class CompilationImpl extends Compilation {
// }

//
//
//
