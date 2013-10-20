/*
 * $Id$
 */
package rebeca.transport;

import java.io.*;

import rebeca.broker.EventSink;

/**
 * @author parzy
 *
 */
public interface StreamSink extends EventSink 
{
	public InputStream getInputStream() throws IOException;
	public OutputStream getOutputStream() throws IOException;
}
