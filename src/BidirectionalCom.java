import java.io.*;
import java.util.*;
import javax.comm.*;//32��Ʈ
import java.util.Scanner;

public class BidirectionalCom{
    static CommPortIdentifier portId;
    static Enumeration portList;

        
    public static void main(String[] args) {
    	String portName = "COM5";
    	if(args.length>0)
    		portName = args[0];
    	
        // �ý��ۿ� �ִ� ������ ����̹��� ����� �޾ƿ´�.
        portList = CommPortIdentifier.getPortIdentifiers();

        // enumeration type �� portList �� ��� ��ü�� ���Ͽ�
        while (portList.hasMoreElements()) {
            // enumeration ���� ��ü�� �ϳ� �����´�.
            portId = (CommPortIdentifier) portList.nextElement();
            // ������ ��ü�� port type �� serial port �̸�
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                
            	if (portId.getName().equals(portName)) {

                    // Linux �� ���
                    //if (portId.getName().equals("/dev/term/a"))
            
                    // ��ü ����
            		SerialPort serialPort = initPort(portId);
            		if(serialPort != null){
            			Sender sender = new Sender(serialPort, portName);
            			Receiver receiver = new Receiver(serialPort, portName);
            		}
                }
            }
        }
    }
    
    static SerialPort initPort(CommPortIdentifier portId){
    	System.out.println("initialize " + portId.getName());
    	SerialPort serialPort = null;
    	try {
            /* ��� �޼ҵ� : 
               public CommPort open(java.lang.String appname, int timeout)
               ��� : 
               ���ø����̼� �̸��� Ÿ�Ӿƿ� �ð� ��� */
        	System.out.println("wait");
            serialPort = (SerialPort) portId.open("BidirectionalComApp", 10000);
        	System.out.println("end");
        } catch (PortInUseException e) {
        	System.out.println("Port open failed.");
        	return serialPort;
        }
    	// �ø��� ��� ����. Data Bit�� 8, Stop Bit�� 1, Parity Bit�� ����.
        try {
            serialPort.setSerialPortParams(9600, 			    		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	    		SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
        	return serialPort;
        }
        
        return serialPort;
    }
 // 
}


class Receiver implements Runnable, SerialPortEventListener{
	InputStream inputStream;
    SerialPort serialPort;
    Thread thread;
    String desc;
	//SimpleRead ������
    public Receiver(SerialPort _serialPort, String _desc) {
    	serialPort = _serialPort;
    	desc = _desc;
    	
     // ������ ��ü ����
        thread = new Thread(this);
        // ������ ����
        thread.start();
    }
    
    public void init(){
    	
        try {
            // �ø��� ��Ʈ���� �Է� ��Ʈ���� ȹ���Ѵ�.
            inputStream = serialPort.getInputStream();
        } catch (IOException e) { }
        // �ø��� ��Ʈ�� �̺�Ʈ �����ʷ� �ڽ��� ����Ѵ�.
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) { }
        
        /* �ø��� ��Ʈ�� �����Ͱ� �����ϸ� �̺�Ʈ�� �� �� �߻��Ǵµ�
           �� ��, �ڽ��� �����ʷ� ��ϵ� ��ü���� �̺�Ʈ�� �����ϵ��� ���. */
        serialPort.notifyOnDataAvailable(true);

    }
    public void run() {
    	init();
    	
	    try {
	        Thread.sleep(20000);
	    } catch (InterruptedException e) { }
	}

    public void serialEvent(SerialPortEvent event) {
        // �̺�Ʈ�� Ÿ�Կ� ���� switch ������ ����.
        switch (event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
            
            // �����Ͱ� �����ϸ�
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[20];    // byte �迭 ��ü ����
                int i = 0;
                int len = 20;

                // �Է� ��Ʈ���� ��밡���ϸ�, ���۷� �о� ���� ��
                // String ��ü�� ��ȯ�Ͽ� ���
                try {
                    while (i<len ) {
                        i += inputStream.read(readBuffer, i, len-i);
                        if(readBuffer[i-1] == '\n')
                        	break;
                    }
                    System.out.print(desc + "> ");
                    System.out.print(new String(readBuffer, 0, i));
                } catch (IOException e) { }
                break;
        }
    }
}

class Sender implements Runnable{
	OutputStream outputStream;
    SerialPort serialPort;
    Thread thread;
    String desc;
	//SimpleRead ������
    public Sender(SerialPort _serialPort, String _desc) {
    	serialPort = _serialPort;
    	desc = _desc;
    	
     // ������ ��ü ����
        thread = new Thread(this);
        // ������ ����
        thread.start();
    }
    
    public void init(){
    	
        try {
            // �ø��� ��Ʈ���� �Է� ��Ʈ���� ȹ���Ѵ�.
        	outputStream = serialPort.getOutputStream();
        } catch (IOException e) { }
        // �ø��� ��Ʈ�� �̺�Ʈ �����ʷ� �ڽ��� ����Ѵ�.
        
    }
    public void run() {
    	init();
    	

    	String message;
        Scanner scan = new Scanner(System.in);      // ���� �Է��� ���ڷ� Scanner ����
        
        
        try{   
		    while(true){
		    	message = scan.nextLine();            // Ű���� ���� �Է�
		    	message += "\n";
		        System.out.println("�Է� �޽���: \""+ message + "\"");
		        	
		    	outputStream.write(message.getBytes());
		    }
    	} catch (IOException e) { }
	}
}