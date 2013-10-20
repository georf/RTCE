/**
 * 
 */
package rebeca.peernet;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class BriteParser implements Control 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final Logger LOG = Logger.getLogger(BriteParser.class);
	
	public static final String PAR_NETWORK = "network";
	public static final String PAR_FILE = "file";
	
	private static final String DEFAULT_FILE = "topology";
	

	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private int nid;
	private String file;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	public BriteParser()
	{
		file = null;
		nid = -1;
	}
	
	public BriteParser(String prefix)
	{
		file = Configuration.getString(prefix + "." + PAR_FILE, DEFAULT_FILE);
		nid = Configuration.getPid(prefix + "." + PAR_NETWORK);
	}
	
	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override 
	public boolean execute()
	{
		// get file
		String resource = this.file;
		
		// get graph
		Node node = Network.prototype;
		BriteNetwork net = (BriteNetwork)node.getProtocol(nid);
		Graph graph = net.getGraph();

		boolean rst = parse(resource,graph);
		if (rst && LOG.isInfoEnabled())
		{
			LOG.info("'" + resource + "' parsed successfully");
		}
		return rst;
	}
	
	
	// parser logic -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public boolean parse(String resource, Graph graph)
	{
		return parse(getResourceAsStream(resource),graph);
	}
	
	
	public boolean parse(InputStream input, Graph graph)
	{
		// get properties or adds them if not existent
		Graph.Property x = graph.addProperty(BriteNetwork.PAR_X);
		Graph.Property y = graph.addProperty(BriteNetwork.PAR_Y);
		Graph.Property in = graph.addProperty(BriteNetwork.PAR_IN);
		Graph.Property out = graph.addProperty(BriteNetwork.PAR_OUT);
		Graph.Property as = graph.addProperty(BriteNetwork.PAR_AS);
		Graph.Property type = graph.addProperty(BriteNetwork.PAR_TYPE);
		Graph.Property length = graph.addProperty(BriteNetwork.PAR_LENGTH);
		Graph.Property delay = graph.addProperty(BriteNetwork.PAR_DELAY);
		Graph.Property bandwidth = graph.addProperty(BriteNetwork.PAR_BANDWIDTH);		
				
		try
		{
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(input));
			String line = null;
			String[] tokens = null;
			HashMap<Integer,Graph.Vertex> vertices = null;
			int id = 0;
			
			// skip preamble and model header
	    	while((line=reader.readLine()) != null)
	    	{
	    		line = line.trim();
	    		if (line.equals("")) continue;
	    		if (line.startsWith("#")) continue;
	    		if (line.startsWith("Nodes:")) break;
	    	}
	    	if (line==null)
	    	{
	    		LOG.error("unexpected end of file: " + reader + ", line " 
	    				+ reader.getLineNumber());
	    		return false;
	    	}
	    	
	    	// determine number of vertices
    		tokens = line.split(" ");
	    	try
	    	{
	    		int n = Integer.parseInt(tokens[2]);
	    		vertices = new HashMap<Integer,Graph.Vertex>(n);
	    	}
	    	catch (Exception e)
	    	{
	    		LOG.error("parse error: " + reader + ", line " 
	    				+ reader.getLineNumber());
	    		return false;
	    	}
	    	
	    	// parse vertices
	    	while((line=reader.readLine()) != null)
	    	{
	    		line = line.trim();
	    		if (line.equals("")) continue;
	    		if (line.startsWith("#")) continue;
	    		if (line.startsWith("Edges:")) break;
	    	
	    		Graph.Vertex v = graph.addVertex();
    			tokens = line.split("\t");
    			try
	    		{
    				id = Integer.parseInt(tokens[0]);
    				//v.set(this.nid,id);
    				v.set(x,Float.parseFloat(tokens[1]));
    				v.set(y,Float.parseFloat(tokens[2]));
    				v.set(in,Integer.parseInt(tokens[3]));
    				v.set(out,Integer.parseInt(tokens[4]));
    				v.set(as,Integer.parseInt(tokens[5]));
    				v.set(type,tokens[6]);
	    		}
    	    	catch (Exception e)
    	    	{
    	    		LOG.error("parse error: " + reader + ", line " 
    	    				+ reader.getLineNumber());
    	    		return false;
    	    	}
	    		vertices.put(id,v);
	    	}
	    	if (line==null)
	    	{
	    		LOG.error("unexpected end of file: " + reader + ", line " 
	    				+ reader.getLineNumber());
	    		return false;
	    	}
	    	
	    	// parse edges
	    	while((line=reader.readLine()) != null)
	    	{
	    		line = line.trim();
	    		if (line.equals("")) continue;
	    		if (line.startsWith("#")) continue;
	    	
    			tokens = line.split("\t");
    			try
	    		{
    				id = Integer.parseInt(tokens[0]);
    				Graph.Vertex src = vertices.get(Integer.parseInt(tokens[1]));
    				Graph.Vertex dst = vertices.get(Integer.parseInt(tokens[2]));
    				Graph.Edge e = graph.addEdge(src,dst);
    				e.set(length,Double.parseDouble(tokens[3]));
    				e.set(delay,Double.parseDouble(tokens[4]));
    				e.set(bandwidth,Double.parseDouble(tokens[4]));
    				e.set(type,tokens[7]);
	    		}
    	    	catch (Exception e)
    	    	{
    	    		LOG.error("parse error: " + reader + ", line " 
    	    				+ reader.getLineNumber());
    	    		return false;
    	    	}
	    	}
		}
		catch (IOException e)
		{
			LOG.error("parse exception",e);
			return false;
		}
		
		return true;
	}
	
	
	// helpers ----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected InputStream getResourceAsStream(String name)
	{
		InputStream in = null;
		
		// try to load as file
		try { in = new FileInputStream(new File(name)); } catch (FileNotFoundException e) { }
		if (in != null) return in;
		
		// try to load as system resource
		in = ClassLoader.getSystemResourceAsStream(name);
		if (in != null) return in;
		
		throw new RuntimeException("resource not found: "+name);
	}
}
