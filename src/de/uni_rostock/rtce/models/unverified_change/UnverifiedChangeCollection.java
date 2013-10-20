package de.uni_rostock.rtce.models.unverified_change;

import java.util.ArrayList;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

public class UnverifiedChangeCollection {

	/**
	 * A map that is used to store the entries by it's global versions.
	 */
	private ArrayList<UnverifiedChangeCollectionEntry> list = new ArrayList<UnverifiedChangeCollectionEntry>();

	public void add(UnverifiedChangeCollectionEntry entry) {
		list.add(entry);
	}

	public int size() {
		return list.size();
	}

	public void removeFirst() {
		list.remove(0);
	}

	public boolean isBefore(int offset) {
		for (UnverifiedChangeCollectionEntry e : list) {
			if (e.getChangeEvent().getOffset() + e.getChangeEvent().getLength() >= offset) {
				return true;
			}
		}
		return false;
	}

	public ChangeEvent[] getBackwardsChangeEvents() {
		ArrayList<ChangeEvent> events = new ArrayList<ChangeEvent>(list.size());
		for (int i = list.size() - 1; i > -1; i--) {

			ChangeEvent help = list.get(i).getChangeEvent();
			events.add(new ChangeEvent(help.getDocumentId(), help.getUser(),
					help.getOffset(), help.getText().length(), list.get(i)
							.getDeletedText(), help.getLocalVersion(), help
							.getReferenceGlobalVersion()));
		}
		return events.toArray(new ChangeEvent[0]);
	}

	public int getCorrectedOffset(int conflictOffset) {
		int offset = conflictOffset;
		for (UnverifiedChangeCollectionEntry entry : list) {
			if (offset > entry.getChangeEvent().getOffset()) {
				offset += entry.getChangeEvent().getText().length();
				offset -= entry.getChangeEvent().getLength();
			}
		}

		return offset;
	}

	public ChangeEvent[] getChangeEvents() {
		return list.toArray(new ChangeEvent[0]);
	}

	public void applyPatch(ChangeEvent conflictChange) {
		for (UnverifiedChangeCollectionEntry entry : list) {

			ChangeEvent event = entry.getChangeEvent();
			/*
			 * Correct the offset
			 */
			if (conflictChange.getOffset() <= event.getOffset()) {
				event.setOffset(event.getOffset() - conflictChange.getLength()
						+ conflictChange.getText().length());
			}
		}
	}
}
