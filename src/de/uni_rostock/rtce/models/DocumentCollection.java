package de.uni_rostock.rtce.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Handles a collection of Documents
 * 
 * @see Document
 * @see RemoteDocument
 * @see SharedDocument
 * 
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 * @param <K>
 *            Document
 */
public class DocumentCollection<K extends Document> extends Observable
		implements Collection<K>, IPropertyChangeListener {

	protected ArrayList<Document> documents = new ArrayList<Document>(8);

	public DocumentCollection() {
		super();
	}

	/**
	 * Returns the document if found in the list, otherwise null
	 * 
	 * @param documentId
	 * @return
	 */
	public Document getByDocumentId(String documentId) {

		for (Document d : documents) {
			if (d.getDocumentId().equals(documentId))
				return d;
		}
		return null;
	}

	@Override
	public boolean add(K element) {

		// wrong type
		if (!(element instanceof Document))
			return false;

		// key exists
		if (getByDocumentId(((Document) element).getDocumentId()) == null) {

			documents.add((Document) element);

			((Document) element).addPropertyChangeListener(this);

			setChanged();
			notifyObservers();

			return true;
		}

		return false;
	}

	@Override
	public boolean remove(Object element) {

		boolean changed = documents.remove(element);

		if (changed) {
			setChanged();
			notifyObservers();
		}

		return changed;
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		boolean changed = false;

		for (K o : c) {
			if (add(o))
				changed = true;
		}
		return changed;
	}

	@Override
	public void clear() {
		documents.clear();
		setChanged();
		notifyObservers();
	}

	@Override
	public boolean contains(Object o) {
		return documents.contains(o);
	}

	/**
	 * @deprecated Use {@link #getByTextEditor(ITextEditor)} instead.
	 * @param editor
	 * @return
	 */
	public boolean contains(ITextEditor editor) {
		for (Document d : documents) {
			if (d.getTextEditor() == null)
				continue;

			if (d.getTextEditor().equals(editor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return documents.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return documents.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<K> iterator() {
		return (Iterator<K>) documents.iterator();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = documents.removeAll(c);

		if (changed) {
			setChanged();
			notifyObservers();
		}

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = documents.retainAll(c);

		if (changed) {
			setChanged();
			notifyObservers();
		}

		return changed;
	}

	@Override
	public int size() {
		return documents.size();
	}

	@Override
	public Object[] toArray() {
		return documents.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return documents.toArray(a);
	}

	/**
	 * Returns the document for the given text editor
	 * 
	 * @param part
	 *            texteditor to search for
	 * @return null if nothing was found
	 */
	public Document getByTextEditor(ITextEditor part) {

		for (Document d : documents) {
			if (d.getTextEditor() == null) {
				continue;
			}

			if (d.getTextEditor().equals(part)) {
				return d;
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(Document.KEY_FILENAME_CHANGED)
				|| property.equals(Document.KEY_OWNER_CHANGED)
				|| property.equals(Document.KEY_SUPPORTER_COUNT_CHANGED)
				|| property.equals(Document.KEY_DOCUMENT_ID_CHANGED)) {
		
			setChanged();
			notifyObservers();
		}
	}

}
