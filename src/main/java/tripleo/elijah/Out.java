/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.ParserClosure;
import tripleo.elijah.util.Helpers;
import tripleo.elijah.util.TabbedOutputStream;
import tripleo.elijah_fluffy.util.Eventual;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Out {
	private final Compilation   compilation;
	private final ParserClosure pc;

	public Out(final String fn, final Compilation compilation, final boolean do_out) {
		this.pc          = new ParserClosure(fn, compilation);
		this.compilation = compilation;
	}

	public record OS_ModuleX(OS_Module module, IOException exc) {
	}

	//@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("NM_METHOD_NAMING_CONVENTION")
	public void FinishModule() {
		println("** FinishModule");

		Eventual<OS_ModuleX> ev = new Eventual<>();
		ev.resolve(new OS_ModuleX(pc.module, fmw()));

		ev.onFail(X->{throw new Error();});
		ev.then(pc->{compilation.put_module(pc.module.getFileName(), pc.module);});
	}

	private @Nullable IOException fmw() {
		try {
			// pc.module.print_osi(tos);


			final @NotNull SimpleDateFormat sdf      = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			final String                    filename = String.format("eljc-%s.out", sdf.format(new Date()));
			final TabbedOutputStream        tos      = new TabbedOutputStream(new FileOutputStream(filename));

			tos.put_string_ln(pc.module.getFileName());
			Helpers.printXML(pc.module, tos);
			tos.close();

			return null;
		} catch (final FileNotFoundException aException) {
			println("&& FileNotFoundException");
			return aException;
		} catch (final IOException aException) {
			println("&& IOException");
			return aException;
		}
	}

	public static void println(final String s) {
		// compilation.addMarker(Markers.Out_FinishModule, s);
		System.out.println(s);
	}

	private final ParserClosure pc;

	public ParserClosure closure() {
		return pc;
	}

	public @NotNull OS_Module module() {
		return pc.module;
	}
}

//
//
//
