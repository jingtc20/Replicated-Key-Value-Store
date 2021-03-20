package socket;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TCP_worker implements Runnable{
	protected Data_store data;
	protected TCP_server ser;
	protected Socket connection;
	String response = "";
	String message = "";
	static List<String> Nodes = new ArrayList();
	String FilePath = "/tmp/nodes.cfg";
	int port;
    
    public TCP_worker(Socket clientSock, Data_store key_value, int port, TCP_server server) {
    	this.port = port;
        this.connection = clientSock;
        this.data = key_value;
        this.ser = server;
    }
	
	public void run() {
		LoadFile(FilePath);
		try {
			// get I/O streams for the connection
			InputStream inputStream = connection.getInputStream();
			OutputStream outputStream = connection.getOutputStream();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(inputStream));
	        DataOutputStream outToClient = new DataOutputStream(outputStream);
	        
	        // read the message from server
	        String commands = inFromClient.readLine();    
	        String[] cmds = commands.split(" ");
	        String operation = cmds[0];
	        
	        // send specific response to client based on the operations
	        switch (operation) {
	            case "put":
	            	response = TwoPC(cmds, "put");
	                break;
	            case "dput1":
	            	response = data.dput1(cmds[1], cmds[2]);
	                break;
	            case "dput2":
	            	response = data.dput2(cmds[1], cmds[2]);
	                break;
	            case "dputabort":
	            	response = data.dputabort(cmds[1]);
	                break;
	            case "get":
	                response = data.get(cmds[1]);
	                break;
	            case "del":
	            	response = TwoPC(cmds, "del");
	                break;
	            case "ddel1":
	            	response = data.ddel1(cmds[1]);
	                break;
	            case "ddel2":
	            	response = data.ddel2(cmds[1]);
	                break;
	            case "ddelabort":
	            	response = data.ddelabort(cmds[1]);
	                break;
	            case "store":
	                response = data.store();
	                break;
	            case "exit":
	            	response = data.exit();
	                ser.stop();
	        }
	        outToClient.writeBytes(response);	        
	        // close I/O streams
	        inputStream.close();
	        outputStream.close();                             
		}catch (IOException e) {
            throw new RuntimeException("Couldn't get I/O for the connection");
        }
	}
	
	
	public String TwoPC(String[] cmds, String operation) {
		// The first phase: voting phase
		message = Voting(cmds, operation);
		if (message.equals("abort")) {
			response = "The entire transaction is aborted";
			return response;
		}else{
			// The second phase: decision phase
			message = Commit(cmds, operation);
			response = message;
			return response;		
		}	
	}
	
	public String rollback(int i, String[] cmds, String operation){
		for (int j=0; j<=i; j++) {
			String NodeIp= Nodes.get(i).split(":")[0];
			int NodePort= Integer.parseInt(Nodes.get(i).split(":")[1]);
			String LeaderIp = "";
			try {
				LeaderIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
			}
			if (NodeIp.equals(LeaderIp) &&  NodePort == port){
				if (operation.equals("put")) {
					message = data.dputabort(cmds[1]);
				}else {
					message = data.ddelabort(cmds[1]);
				}
			}else {
				try {
					Socket socket = new Socket(NodeIp, NodePort);
					String commands = "";
					if (operation.equals("put")) {
						commands = "dputabort" + " " + cmds[1];
					}else {
						commands = "ddelabort" + " " + cmds[1];
					}					
					// Send dputabort/ddelabort command to the TCP_server and read message from the TCP_server
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.writeBytes(commands + "\n");
					out.flush();
					String node_message = reader.readLine();					
					
					// close I/O streams
					out.close();
			        reader.close(); 
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "the abort operation has been done";
	}

	
	public String Voting(String[] cmds, String operation){
		String message = "";
		for (int i = 0; i < Nodes.size(); i++) {
			String NodeIp= Nodes.get(i).split(":")[0];
			int NodePort= Integer.parseInt(Nodes.get(i).split(":")[1]);
			String LeaderIp = "";
			try {
				LeaderIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
			}
			// For the transaction leader
			if (NodeIp.equals(LeaderIp) &&  NodePort == port){
				if (operation.equals("put")) {
					message = data.dput1(cmds[1], cmds[2]);
				}else {
					message = data.ddel1(cmds[1]);
				}
				if (message.equals("abort")) {
					// retry the transaction up to 10 times	
					int n = 0;
					while (n<10) {
						if (message.equals("abort")){
							if (operation.equals("put")) {
								message = data.dput1(cmds[1], cmds[2]);
							}else {
								message = data.ddel1(cmds[1]);
							}
							n += 1;
						}else {
							break;
						}	
					}
					// rollback if the transaction can't be committed after 10 attempts
					if (message.equals("abort")) {
						rollback(i, cmds, operation);
						return message;
					}
				}
			}else {
				// For other nodes that aren't the transaction leader
				try {
					Socket socket = new Socket(NodeIp, NodePort);
					if (operation.equals("put")) {
						cmds[0]= "dput1";
					}else {
						cmds[0]= "ddel1";
					}
					String commands = String.join(" ", cmds);

					// Send dput1/ddel1 command to the TCP_server and read message from the TCP_server
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.writeBytes(commands + "\n");
					out.flush();
					String node_message = reader.readLine();
					// the ouput should be: Phase1: dput1 a 777---acknowledgement
//					System.out.println("Phase1: " + commands + "---" + node_message);					
					
					if (node_message.equals("abort")) {
						// retry the transaction up to 10 times
						int n = 0;
						while (n<10) {
							if (node_message.equals("abort")){
								out = new DataOutputStream(socket.getOutputStream());
					            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								out.writeBytes(commands + "\n");
								out.flush();
								node_message = reader.readLine();
								n += 1;
							}else {
								break;
							}	
						}
						// rollback if the transaction can't be committed after 10 attempts
						if (node_message.equals("abort")) {
							rollback(i, cmds, operation);
							return node_message;
						}
					}
									
					// close I/O streams
					out.close();
			        reader.close(); 
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}							
			}
		}
		return "Phase 1 has been done";
	}
	
    public String Commit(String[] cmds, String operation) {
    	String message = "";
		for (int i = 0; i < Nodes.size(); i++) {
			String NodeIp= Nodes.get(i).split(":")[0];
			int NodePort= Integer.parseInt(Nodes.get(i).split(":")[1]);
			String LeaderIp = "";
			try {
				LeaderIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
			}
			// For the transaction leader
			if (NodeIp.equals(LeaderIp) &&  NodePort == port){
				if (operation.equals("put")) {
					message = data.dput2(cmds[1], cmds[2]);
				}else {
					message = data.ddel2(cmds[1]);
				}
			}else {
				// For other nodes that aren't the transaction leader
				try {
					Socket socket = new Socket(NodeIp, NodePort);
					if (operation.equals("put")) {
						cmds[0]= "dput2";
					}else {
						cmds[0]= "ddel2";
					}
					String commands = String.join(" ", cmds);

					// Send dput2/ddel2 command to the TCP_server and read message from the TCP_server
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.writeBytes(commands + "\n");
					out.flush();
					String node_message = reader.readLine();
					// the ouput should be: Phase2: dput2 a 777---server response:put key=a
//			        System.out.println("Phase2: " + commands + "---" + node_message);					

			        // close I/O streams
			        out.close();
			        reader.close(); 
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
		}
    	return message;		
	} 
    
    public static void LoadFile(String FilePath){
    	Nodes.clear();
    	List<String> allLines;
		try {
			allLines = Files.readAllLines(Paths.get(FilePath));
			for (String line : allLines) {
				Nodes.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 		
    }
	
	
}
	
	
