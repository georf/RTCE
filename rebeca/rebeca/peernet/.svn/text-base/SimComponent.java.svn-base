/**
 * 
 */
package rebeca.peernet;

import peersim.core.*;

/**
 * Interface defining essential simulation methods for network components.
 * @author parzy
 *
 */
public interface SimComponent extends Cloneable
{
	public interface SimEvent
	{
		public SimComponent getComponent();
	}
	
	public void send(SimSink sender, Node src, Node dst, Object msg);
	public void processEvent(Node node, int pid, Object event);
	
	public Object clone();
}
