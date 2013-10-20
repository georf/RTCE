package de.uni_rostock.rtce.models.change_collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

/**
 * @see {@link IChangeCollection}
 * 
 */
public class ChangeCollection implements IChangeCollection {

	/**
	 * A map that is used to store the entries by it's global versions.
	 */
	private Map<Integer, IChangeCollectionEntry> map = new HashMap<Integer, IChangeCollectionEntry>();

	/**
	 * The first entry.
	 */
	private IChangeCollectionEntry first = null;

	/**
	 * The last entry.
	 */
	private IChangeCollectionEntry last = null;

	@Override
	public void clear() {
		first = null;
		last = null;
		map.clear();
	}

	@Override
	public boolean contains(Integer globalVersion) {
		return map.containsKey(globalVersion);
	}

	@Override
	public IChangeCollectionEntry get(Integer globalVersion) {
		return map.get(globalVersion);
	}

	@Override
	public IChangeCollectionEntry getFirst() {
		return first;
	}

	@Override
	public IChangeCollectionEntry getLast() {
		return last;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Puts the given change at the end of the list. It is not ensured that the
	 * global version is really the highest global version! <b>The caller has to
	 * ensure that the given global version is the highest global version!</b>
	 * 
	 * @param changeEvent
	 *            the change
	 * @param globalVersion
	 *            the global version
	 * @return The newly created entry, including the given change and global
	 *         version
	 * @throws GlobalVersionAlreadyUsedException
	 *             if the given global version is used already
	 */
	private IChangeCollectionEntry putLast(ChangeEvent changeEvent,
			Integer globalVersion) throws GlobalVersionAlreadyUsedException {
		IChangeCollectionEntry entry = new ChangeCollectionEntry(changeEvent,
				globalVersion);
		this.putLast(entry);
		return entry;
	}

	/**
	 * Puts the given entry at the end of the collection.
	 * 
	 * @param entry
	 *            the new entry
	 * @throws GlobalVersionAlreadyUsedException
	 *             if the global version of the entry is used already
	 */
	private void putLast(IChangeCollectionEntry entry)
			throws GlobalVersionAlreadyUsedException {
		if (entry == null)
			throw new NullPointerException();

		if (first == null) {
			/*
			 * The Collection is empty
			 */
			first = entry;
			last = entry;
			map.put(entry.getGlobalVersion(), entry);
		} else {
			/*
			 * The collection is not empty
			 */
			Integer globalVersion = entry.getGlobalVersion();
			if (map.containsKey(globalVersion))
				throw new GlobalVersionAlreadyUsedException(
						"The global version " + globalVersion
								+ " is already in use!");

			entry.setBefore(last);
			last.setAfter(entry);
			last = entry;
			map.put(globalVersion, entry);
		}

	}

	@Override
	public int size() {
		return map.size();
	}

	/**
	 * Produces a list with all the {@link IChangeCollectionEntry}s before the
	 * given global version <i>n</i> until an other given global version
	 * <i>m</i>.<br>
	 * So the list will contain the following entries: <i>{k | k&lt;n &amp;&amp;
	 * k&gt;m}</i>, which are sorted ascending (the first entry has the
	 * globalVersion <i>n-1</i>).
	 */
	private List<IChangeCollectionEntry> getAllBeforeUntil(
			IChangeCollectionEntry baseEntry, Integer untilGlobalVersion) {

		/*
		 * Get the necessary until entry.
		 */
		IChangeCollectionEntry untilEntry = get(untilGlobalVersion);

		/*
		 * Cancel if one of these two entries is null.
		 */
		if (baseEntry == null || untilEntry == null)
			return new ArrayList<IChangeCollectionEntry>(0);

		/*
		 * Iterate through all entries that are made between the given global
		 * version and the corresponding referenced global version.
		 */
		ArrayList<IChangeCollectionEntry> result = new ArrayList<IChangeCollectionEntry>();

		IChangeCollectionEntry tempEntry = baseEntry.getBefore();
		while ((tempEntry != null)
				&& (tempEntry.getGlobalVersion() > untilGlobalVersion)) {
			result.add(tempEntry);
			tempEntry = tempEntry.getBefore();
		}

		// TODO eventuell geht das auch besser, aber ich brauche die Liste
		// nunmal umgekehrt
		ArrayList<IChangeCollectionEntry> result2 = new ArrayList<IChangeCollectionEntry>(
				result.size());
		for (int i = result.size() - 1; i >= 0; i--) {
			result2.add(result.get(i));
		}

		return result2;
	}

	/**
	 * Corrects the offset of the given change, that is defined by it's entry.
	 * 
	 * @param entry
	 *            the global change
	 */
	private void correctOffset(IChangeCollectionEntry entry) {

		ChangeEvent changeEvent = entry.getChangeEvent();

		/*
		 * Get all conflicting changes, that were made between the given global
		 * change and the corresponding referenced global version.
		 */
		Integer refGlobalVersion = changeEvent.getReferenceGlobalVersion();

		List<IChangeCollectionEntry> prevEntries = getAllBeforeUntil(entry,
				refGlobalVersion);

		/*
		 * Get the entries change event and extract the offset
		 */
		int newOffset = changeEvent.getOffset();

		int selfChangedOffset = 0;

		for (IChangeCollectionEntry conflictEntry : prevEntries) {
			/*
			 * get the change
			 */
			ChangeEvent conflictChange = conflictEntry.getChangeEvent();

			if (conflictChange.getUserId().equals(changeEvent.getUserId())) {
				if (conflictChange.getOffset() <= changeEvent.getOffset()) {
					selfChangedOffset -= conflictChange.getLength();
					selfChangedOffset += conflictChange.getText().length();
				}
			}
		}

		/*
		 * Check for every conflicting change if the offset has to be corrected
		 * and do so if necessary.
		 */
		for (IChangeCollectionEntry conflictEntry : prevEntries) {
			/*
			 * get the change
			 */
			ChangeEvent conflictChange = conflictEntry.getChangeEvent();

			/*
			 * Ignore all changes from the same user as the given entry
			 */
			if (conflictChange.getUserId().equals(changeEvent.getUserId())) {
				continue;
			}

			/*
			 * Correct the offset
			 */
			int conflictOffset = conflictChange.getOffset();
			if (conflictOffset + selfChangedOffset <= newOffset) {
				newOffset -= conflictChange.getLength();
				newOffset += conflictChange.getText().length();
			}
		}

		/*
		 * The offset can never be zero
		 */
		if (newOffset < 0)
			newOffset = 0;

		if (newOffset != changeEvent.getOffset()) {
			/*
			 * Debug messages
			 * 
			 * String debugText = changeEvent.getText() == "\n" ? "\\n" :
			 * changeEvent.getText();
			 * 
			 * Log.debug("corrected (" + changeEvent.getUser().getName() + ", "
			 * + "|" + changeEvent.getLength() + "|, " + debugText + ", " +
			 * changeEvent.getOffset() + ") " + "to " + newOffset);
			 */
			/*
			 * Set the new offset
			 */
			changeEvent.setOffset(newOffset);
		}
	}

	/**
	 * Puts the given change at the end of this collection and corrects the
	 * offset.
	 * 
	 * @throws GlobalVersionAlreadyUsedException
	 */
	@Override
	public void put(ChangeEvent changeEvent, Integer globalVersion)
			throws GlobalVersionAlreadyUsedException {
		/*
		 * Create an entry and put it as the last element.
		 */
		IChangeCollectionEntry entry = this.putLast(changeEvent, globalVersion);

		/*
		 * Correct the offset of this entry if necessary
		 */
		if (!changeEvent.getUserId().equals(
				Core.getInstance().getLocalUser().getId())) {
			this.correctOffset(entry);
		}
	}
}
