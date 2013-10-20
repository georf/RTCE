package de.uni_rostock.rtce.models.change_collection;

/**
 * Represents the case, that an entry A is stored by the global version X and an
 * other entry B has to be stored by the same global version X.
 * 
 * @author jonas
 * 
 */
public class GlobalVersionAlreadyUsedException extends Exception {

	private static final long serialVersionUID = 347488383266683078L;

	public GlobalVersionAlreadyUsedException(String description) {
		super(description);
	}

}
