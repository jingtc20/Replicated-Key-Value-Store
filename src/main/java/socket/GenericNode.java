/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package genericnode;
package socket;

import java.io.IOException;
import java.sql.Blob;
import java.util.AbstractMap.SimpleEntry;


/**
 *
 * @author wlloyd
 */
public class GenericNode 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    	
        if (args.length > 0){
            if (args[0].equals("tc")){
                System.out.println("TCP CLIENT on port: " + args[2]);
                String addr = args[1];
                int port = Integer.parseInt(args[2]);
                String cmd = args[3];
                String key = (args.length > 4) ? args[4] : "";
                String val = (args.length > 5) ? args[5] : "";
                SimpleEntry<String, String> se = new SimpleEntry<String, String>(key, val);
                
                // insert code to make TCP client request to server at addr:port
                String cmds = "";
                if (args.length == 4) {
                	cmds = cmd;
                }else if (args.length == 5){
                	cmds = cmd + " " + key;
                }else {
                	cmds = cmd + " " + key + " " + val;
                }
                TCP_client client = new TCP_client(addr, port, cmds);  
            	client.start();
            }
            if (args[0].equals("ts")){
                System.out.println("TCP SERVER on port: " + args[1]);
                int port = Integer.parseInt(args[1]);
                
                // insert code to start TCP server on port
                TCP_server server = new TCP_server(port);
                new Thread(server).start();                
            }
        }else{
            String msg = "GenericNode Usage:\n" +
                         "TCP Client:\n" +
                         "tc <address> <port> put <key> <msg>: Put an object into store\n" + 
                         "tc <address> <port> get <key>: Get an object from store by key\n" + 
                         "tc <address> <port> del <key>: Delete an object from store by key\n" + 
                         "tc <address> <port> store: Display object store\n" + 
                         "tc <address> <port> exit: Shutdown server\n" + 
                         "TCP Server:\n" +
                         "ts <port>: run server on <port>\n" +
                         "membership tracking method:\n" +
                         "dynamic config file (“/tmp/nodes.cfg”)";
            System.out.println(msg);
        }  
    }
}
