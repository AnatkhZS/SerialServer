package ui;

import java.io.*;
import java.util.Iterator;

import org.json.*;

public class Test {
   public static void main(String args[]) {
	   try {
		BufferedReader br = new BufferedReader(new FileReader("./config.json"));
		String data = "";
		String str = null;
		while((str=br.readLine())!=null)
			data = data+str+"\n";
		JSONObject dataJson = new JSONObject(data);
		JSONObject serialPorts = dataJson.getJSONObject("SerialPorts");
		Iterator<String> it = serialPorts.keys();
		while(it.hasNext()) {
			String portName = it.next();
			JSONObject serialPort = serialPorts.getJSONObject(portName);
			String name = serialPort.getString("name");
			System.out.println(name);
			int buadrate = serialPort.getInt("buadrate");
			String logPath = serialPort.getString("logPath");
			boolean isRecord = serialPort.getBoolean("isRecord");
		}
		br.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   private boolean isContainedItem(String filename, String serialPort) {
	   try {
		   BufferedReader br = new BufferedReader(new FileReader(filename));
		   String data = "";
		   String str = null;
		   while((str=br.readLine())!=null)
			   data = data+str+"\n";
		   JSONObject dataJson = new JSONObject(data);
		   JSONObject serialPorts = dataJson.getJSONObject("SerialPorts");
		   Iterator<String> it = serialPorts.keys();
		   while(it.hasNext()) {
			   String portName = it.next();
			   if(portName == serialPort) {
				   br.close();
				   return true;
			   }
		   }
		   br.close();
	   } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   return false;
   }
}

