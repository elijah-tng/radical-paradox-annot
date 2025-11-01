/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 8/3/21 3:46 AM
 */
@SuppressWarnings("unused")
public class ElLog {
	private final List<LogEntry> entries = new ArrayList<>();
	private final String         fileName;
	private final String         phase;
	private final Verbosity      verbose;

	public enum Verbosity {
		SILENT, VERBOSE
	}

	public ElLog(final String aFileName, final Verbosity aVerbose, final String aPhase) {
		fileName = aFileName;
		verbose  = aVerbose;
		phase    = aPhase;
	}

	public void err(final String aMessage) {
		final long     time     = System.currentTimeMillis();
		final LogEntry logEntry = new LogEntry(time, LogEntry.Level.ERROR, aMessage);
		addEntry(logEntry);

		behaviorErr(verbose, logEntry, time, aMessage);
	}

	public void addEntry(final LogEntry logEntry) {
		entries.add(logEntry);
	}

	public void info(final String aMessage) {
		final long     time     = System.currentTimeMillis();
		final LogEntry logEntry = new LogEntry(time, LogEntry.Level.INFO, aMessage);
		addEntry(logEntry);
		behaviorInfo(verbose, logEntry, time, aMessage);
	}

	public void behaviorErr(final Verbosity aVerbosity,
													final LogEntry aLogEntry,
													final long aTime,
													final String aMessage) {
		if (aVerbosity == Verbosity.VERBOSE) {
			System.err.println(aMessage);
		}
	}

	public void behaviorInfo(final Verbosity aVerbosity,
													 final LogEntry aLogEntry,
													 final long aTime,
													 final String aMessage) {
		if (aVerbosity == Verbosity.VERBOSE) {
			System.out.println(aMessage);
		}
	}

	public List<LogEntry> getEntries() {
		return entries;
	}

	public String getFileName() {
		return fileName;
	}

	public String getPhase() {
		return phase;
	}

	public Verbosity getVerbose() {
		return verbose;
	}
}

//
//
//
