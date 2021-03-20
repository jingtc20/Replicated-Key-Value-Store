package socket;

import java.io.*;
import java.net.*;

public class TCP_client {
	private BufferedReader reader;
	private DataOutputStream out;
	int port;
	String addr;
    String cmds;
    
    public static void main(String[] args) throws IOException{
//		TCP_client cli = new TCP_client("127.0.0.1", 1234, "put a 777");
//		 TCP_client cli = new TCP_client("127.0.0.1", 1234, "exit");
//		 TCP_client cli = new TCP_client("127.0.0.1", 1234, "put b 888");
//		 TCP_client cli = new TCP_client("127.0.0.1", 1234, "get a");
//		 TCP_client cli = new TCP_client("127.0.0.1", 1234, "del a");
//		 TCP_client cli = new TCP_client("127.0.0.1", 1234, "store");	 
//		cli.start();
	}
	
    public TCP_client(String addr, int port, String commands) {
        this.addr = addr;
        this.port = port;
        this.cmds = commands;
    }
    
	public void start() throws IOException{
		// make a connection to the TCP_server at the port number 
        Socket sock = new Socket(addr, port);
        InputStream inputStream = sock.getInputStream();
		OutputStream outputStream = sock.getOutputStream();
		
		// Send data to the TCP_server
        out = new DataOutputStream(outputStream);
		out.writeBytes(cmds + "\n");
        
		// Read data from the TCP_server
        reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder contents = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
        	contents.append("\n");
        	contents.append(line);
        }
        if (contents.length() > 0) {
        	contents.deleteCharAt(0);
        }
        String res = contents.toString();
        System.out.println(res);
        
        out.close();
        reader.close(); 
    }
}
