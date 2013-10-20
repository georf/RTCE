package de.uni_rostock.rtce.rebeca;

import rebeca.Broker;
import rebeca.broker.BasicProcessingEngine;
import rebeca.broker.ExtendableConfigurationEngine;
import rebeca.component.BasicComponentEngine;
import rebeca.routing.BasicMatchingEngine;
import rebeca.routing.SimpleAdvertising;
import rebeca.routing.SimpleRouting;
import rebeca.transport.ObjectSerializationEngine;
import rebeca.transport.TcpEngine;

/**
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 *
 */
public class RtceConfiguration extends ExtendableConfigurationEngine {
	protected int port;

	public RtceConfiguration(int p) {
		port = p;
	}

	public Object configure(Broker broker) {
		broker.plug(new BasicProcessingEngine()); // event processing
		broker.plug(new BasicMatchingEngine()); // event matching
		broker.plug(new SimpleRouting()); // event routing
		broker.plug(new SimpleAdvertising()); // optional advertising
		broker.plug(new ObjectSerializationEngine()); // event serialization
		broker.plug(new TcpEngine(port)); // event transport
		broker.plug(new BasicComponentEngine()); // components
		return broker;
	}
}
