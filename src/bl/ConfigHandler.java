package bl;

import java.io.*;
import java.util.Iterator;

import org.json.JSONObject;

public class ConfigHandler {
	private String filename;
	private JSONObject root;

	public ConfigHandler(String filename) {
		this.filename = filename;
		init();
	}
	
	private void init() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String data = "";
			String str = null;
			while((str=br.readLine())!=null)
				data = data+str+"\n";
			br.close();
			root = new JSONObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean contain(String item) {
		JSONObject serialPorts = root.getJSONObject("SerialPorts");
		Iterator<String> it = serialPorts.keys();
		while(it.hasNext()) {
			String portName = it.next();
			if(portName.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	public Object query(String serialPort, String key) {
		JSONObject serialPorts = root.getJSONObject("SerialPorts");
		Iterator<String> it = serialPorts.keys();
		while(it.hasNext()) {
			if(it.next().equals(serialPort)) {
				JSONObject serial = serialPorts.getJSONObject(serialPort);
				return serial.get(key);
			}
		}
		return null;
	}
	
	public boolean addSerial(String serialPort) {
		JSONObject serialPorts = root.getJSONObject("SerialPorts");
		Iterator<String> it = serialPorts.keys();
		while(it.hasNext()) {
			if(it.next().equals(serialPort)) 
				return false;
		}
		JSONObject buffer = new JSONObject();
		serialPorts.put(serialPort, buffer);
		return true;
	}
	
	public boolean setValue(String serialPort, String key, Object value) {
		JSONObject serialPorts = root.getJSONObject("SerialPorts");
		Iterator<String> it = serialPorts.keys();
		while(it.hasNext()) {
			if(it.next().equals(serialPort)) {
				JSONObject serial = serialPorts.getJSONObject(serialPort);
				switch(key) {
				case "name":
					serial.put(key, (String)value);
					return true;
				case "buadrate":
					serial.put(key, (int)value);
					return true;
				case "logPath":
					serial.put(key, (String)value);
					return true;
				case "isRecord":
					serial.put(key, (boolean)value);
					return true;
				case "isStartAtMidnight":
					serial.put(key, (boolean)value);
					return true;
				case "isAppendToFile":
					serial.put(key, (boolean)value);
					return true;
				}
			}
		}
		return false;
	}
	
	public void save() {
		String ws = root.toString();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(ws);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
