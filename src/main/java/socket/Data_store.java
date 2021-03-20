package socket;

import java.util.*;

// support five basic Key-Value operations
public class Data_store {
	public HashMap< String, String> data = new HashMap< String, String>();
	String response = "";
	String message = "";
	List Locked = new ArrayList();
	
	// support put operation: Put a value to the key-value store.
	public synchronized  String put(String key, String val) {
        data.put(key, val);
        response = "server response:put key=" + key;
        return response;
    }
	
	public synchronized  String dput1(String key, String val) {
		if (Locked.contains(key)){
			message = "abort";
		}else {
			Locked.add(key);
			message = "acknowledgement";
		}
        return message;
    }
	
	public synchronized  String dput2(String key, String val) {
		message = put(key, val);
		Locked.remove(key);
		return message;	
	}
	
	public synchronized String dputabort(String key) {
		String message = "abort";
		Locked.remove(key);
		return message;
	}

	// support get operation: Return the value stored at <key>.
    public synchronized String get(String key) {
        String val = data.get(key);
        if (val == null) {
        	response = "Error: no that key.";
        }else {
        response = "server response:get key=" + key + " val=" + val;
        }
        return response;
    } 

    // support del operation: Delete the value stored at <key>.
    public synchronized String del(String key) {
        String val = data.remove(key);
        if (val == null) {
        	response = "Error: no that key.";     
        } else {
        	response = "server response:delete key=" + key;
        }
        return response;
    }
    
    public synchronized  String ddel1(String key) {
		if (Locked.contains(key)){
			message = "abort";
		}else {
			Locked.add(key);
			message = "acknowledgement";
		}
        return message;
    }
	
	public synchronized  String ddel2(String key) {
		message = del(key);
		Locked.remove(key);
		return message;	
	}
	
	public synchronized String ddelabort(String key) {
		String message = "abort";
		Locked.remove(key);
		return message;
	}

    // support store operation: Print the contents of the entire key-value store.
    public synchronized String store() {
        StringBuilder contents = new StringBuilder();
        contents.append("server response:");

        Set<String> keys = data.keySet();
        String content = "";
        for(String key: keys){
        	content = "\n" + "key:" + key + ":value:" + data.get(key) + ":";
        	contents.append(content);
        }
        
        // truncate the output after returning 65,000 characters.
        int len  = contents.length();
        String response = contents.toString();
        if (len > 65000) {
        	response = "TRIMMED:\n" + response.substring(0, 65000);
        }
        return response;    
    }
    
    // support exit operation: Close the server
    public synchronized String exit() {
    	response = "<the server then exits>";
    	return response;
    }
}
     


     
