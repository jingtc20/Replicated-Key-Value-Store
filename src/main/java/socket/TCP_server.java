package socket;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TCP_server implements Runnable {
	int port;
	private ServerSocket serverSock;
    private Socket clientSock;
    protected Data_store data;
    protected boolean running = true;
    
	
	public static void main(String[] args) throws IOException{
//		TCP_server ser1 = new TCP_server(1234);
//		TCP_server ser2 = new TCP_server(1235);
//		TCP_server ser3 = new TCP_server(1236);
//		new Thread(ser1).start();
//		new Thread(ser2).start();
//		new Thread(ser3).start();
	}
	
	public TCP_server(int port) {
        this.port = port;
    }
	
	public void run (){	
		// start the TCP_server on the port number.
	  	try {
	  		serverSock =  new ServerSocket(port);
	  		data = new Data_store();	 
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port);
        }
		
		while (running) {
			try {
				// the TCP_server blocks until a client makes a connection request to it.
				clientSock = serverSock.accept();
            } catch (IOException e) {
            		throw new RuntimeException("Cannot accept client connection", e);   
            }
			new Thread(new TCP_worker(clientSock, data, port, this)).start();				
		}
	}
	
	// close the server
	public synchronized void stop() throws IOException{
		running = false;
		serverSock.close();
    }
}
