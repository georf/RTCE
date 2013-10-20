package rebeca;

public interface Component 
{
//	public Object getKey();
	public ContactProfile getContactProfile();
	public void notify(Event event);
	public void setBroker(ComponentBroker broker);
	public ComponentBroker getBroker();
	public void init();
	public void activate();
	public void passivate();
	public void exit();
}
