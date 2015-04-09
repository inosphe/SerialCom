import java.io.*;
import java.util.*;
import javax.comm.*;//32��Ʈ

public class SimpleRead implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;

    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;
    
    public static void main(String[] args) {
        // �ý��ۿ� �ִ� ������ ����̹��� ����� �޾ƿ´�.
        portList = CommPortIdentifier.getPortIdentifiers();

        // enumeration type �� portList �� ��� ��ü�� ���Ͽ�
        while (portList.hasMoreElements()) {
            // enumeration ���� ��ü�� �ϳ� �����´�.
            portId = (CommPortIdentifier) portList.nextElement();
            // ������ ��ü�� port type �� serial port �̸�
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                
            	if (portId.getName().equals("COM5")) {

                    // Linux �� ���
                    //if (portId.getName().equals("/dev/term/a"))
            
                        // ��ü ����
                        SimpleRead reader = new SimpleRead();
                    }
                }
            }
        }
 // SimpleRead ������
    public SimpleRead() {
        try {
            /* ��� �޼ҵ� : 
               public CommPort open(java.lang.String appname, int timeout)
               ��� : 
               ���ø����̼� �̸��� Ÿ�Ӿƿ� �ð� ��� */
            serialPort = (SerialPort) portId.open("SimpleReadApp", 10000);
        } catch (PortInUseException e) { }
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

        // �ø��� ��� ����. Data Bit�� 8, Stop Bit�� 1, Parity Bit�� ����.
        try {
            serialPort.setSerialPortParams(9600, 			    		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	    		SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) { }
        
     // ������ ��ü ����
        readThread = new Thread(this);

        // ������ ����
        readThread.start();
    }
    public void run() {
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
                int numBytes = 0;

                // �Է� ��Ʈ���� ��밡���ϸ�, ���۷� �о� ���� ��
                // String ��ü�� ��ȯ�Ͽ� ���
                try {
                    while (inputStream.available() > 0) {
                        numBytes = 							    inputStream.read(readBuffer);
                    }
                    System.out.print(new String(readBuffer, 0, numBytes));
                } catch (IOException e) { }
                break;
            }
        }
    }

