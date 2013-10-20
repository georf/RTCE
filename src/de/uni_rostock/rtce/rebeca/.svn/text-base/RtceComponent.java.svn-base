package de.uni_rostock.rtce.rebeca;

import rebeca.Event;
import rebeca.component.AbstractComponent;
import rebeca.filter.BasicFilter;
import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;

/**
 * Component for Rebeca
 * 
 * Used to publish events and notify about events. Normaly everything is managed
 * by {@link RebecaController}.
 * 
 * @see RebecaController
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public class RtceComponent extends AbstractComponent {

	/**
	 * Called only from Rebeca to notify about events
	 */
	public void notify(Event event) {
		if (event instanceof RtceEvent) {

			/*
			 *  Check for an event of me
			 */
			if (!((RtceEvent) event).getUserId().equals(
					Core.getInstance().getLocalUser().getId())) {
				RebecaController.getInstance().fireEvent((RtceEvent) event);
			}
		}
	}
	
	/**
	 * Advertise a filter.
	 * @param f the filter
	 */
	public void advertise(BasicFilter f){
		broker.advertise(f);
	}
	
	/**
	 * Unadvertise a filter.
	 * @param f the filter
	 */
	public void unadvertise(BasicFilter f){
		broker.unadvertise(f);
	}

	/**
	 * Subscribe to a filter.
	 * @param f the filter
	 */
	public void subscribe(BasicFilter f) {
		broker.subscribe(f);
	}

	/**
	 * Unsubscribe to a filter.
	 * @param f the filter
	 */
	public void unsubscribe(BasicFilter f) {
		broker.unsubscribe(f);
	}
}
