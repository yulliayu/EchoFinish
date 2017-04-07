package echo.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	
	JPanel p_north;
	JTextField t_port, t_msg;
	JButton  bt_start;
	JTextArea  area;
	JScrollPane  scroll;
	int port=7777;
	ServerSocket  server; // ���� ������ ����	
	
	Thread  thread; // ���� ������ ������
	BufferedReader  buffr;
	BufferedWriter  buffw;
	
	public ServerMain() {
		
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 15);
		bt_start = new JButton("����");
		area = new JTextArea(15,15);
		scroll = new JScrollPane(area);
		
		t_msg = new JTextField();
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);

		setVisible(true);
		setBounds(600, 300, 300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	// ���� ������ ����
	// ������ ���� - checked Exception : ����ó�� ����
	//                   runtime Exception  : ����ó�� �������� ����
	public void startServer(){
		bt_start.setEnabled(false);
		try {
			port = Integer.parseInt(t_port.getText());
			server = new ServerSocket(port); // ���� ����
			area.append("���� �غ��..\n");
			System.out.println(area.getText());
			
			// ����
			// ����ζ� �Ҹ��� ���ξ������ ���� ���ѷ����� ���, �������¿� ������ �ؼ��� �ȵȴ�.
			// ����δ� �������� �̺�Ʈ�� �����Ѵٰų�, ���α׷��� ��ؾ� �ϹǷ�
			// ���ѷ����� ��⿡ ������ ������ ������ �� ���� ���� �ȴ�.
			// ����Ʈ�� ���ߺо߿����� �̿Ͱ��� �ڵ�� �̹� ������Ÿ�Ӻ��� �����߻���. 
			Socket socket = server.accept(); // ���Ѵ�� ����
			area.append("���� ����..\n");
			//System.out.println(area.getText());
			
			// Ŭ���̾�Ʈ�� ��ȭ�� �ϱ� ���� �����Ѱ��̹Ƿ�, ������ �Ǵ� ���� ��Ʈ���� ������.
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			
			String data;
			while (true){
				data = buffr.readLine();  // Ŭ���̾�Ʈ�� �޼��� �ޱ�
				area.append("Ŭ���̾�Ʈ�� �� : "+data+"\n"); 
				
				buffw.write(data+"\n"); // Ŭ���̾�Ʈ�� �޼��� ������
				buffw.flush();  // ���ۺ���
			}
			
		} catch (NumberFormatException e) {
			// �������� �ʴ� ����. RuntimeException
			JOptionPane.showMessageDialog(this, "��Ʈ�� ���ڷ� �־��");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		thread = new Thread(){
		
			public void run() {
				startServer();
			}
		};			
		thread.start();
	}

	public static void main(String[] args) {
		new ServerMain();
	}
}
