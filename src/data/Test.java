package data;

import java.io.*;
import java.util.*;

import com.fazecast.jSerialComm.*;

public class Test {
	public static void main(String args[]) {
//		SerialPort[] lst = SerialPort.getCommPorts();
//		for(SerialPort sp:lst) {
//			System.out.println(sp.getPortDescription());
//		}
//		Properties props = System.getProperties();
//		System.out.println(props.getProperty("os.name"));
//		for(String s :new Test().getSerialPortList())
//			System.out.println(s);
//		SerialOpener serial1 = new SerialOpener("/dev/cu.SLAB_USBtoUART", 57600);
//		SerialOpener serial2 = new SerialOpener("/dev/cu.SLAB_USBtoUART", 57600);
	}
	
	public String[] getSerialPortList() {
		Process p = null;
        List<String> result = new ArrayList<String>();
        try {
            p = new ProcessBuilder("ls", "/dev").start();
        } catch (Exception e) {
        	System.out.println("Error");
            return null;
        }
        //读取进程输出值
        InputStream inputStream = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        try {
            while ((s = br.readLine()) != null) {
            	if(s.startsWith("cu"))
                	result.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toArray(result);
	}
	
	public String[] toArray(List<String> lst) {
		String[] result = new String[lst.size()];
		int i = 0;
		for(String s:lst) {
			result[i] = s;
			i++;
		}
		return result;
	}
}
