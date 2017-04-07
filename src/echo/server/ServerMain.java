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
	ServerSocket  server; // 접속 감지용 소켓	
	
	Thread  thread; // 서버 가동용 쓰레드
	BufferedReader  buffr;
	BufferedWriter  buffw;
	
	public ServerMain() {
		
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 15);
		bt_start = new JButton("가동");
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
	
	// 서버 생성및 가동
	// 예외의 종류 - checked Exception : 예외처리 강요
	//                   runtime Exception  : 예외처리 강요하지 않음
	public void startServer(){
		bt_start.setEnabled(false);
		try {
			port = Integer.parseInt(t_port.getText());
			server = new ServerSocket(port); // 서버 생성
			area.append("서버 준비됨..\n");
			System.out.println(area.getText());
			
			// 가동
			// 실행부라 불리는 메인쓰레드는 절대 무한루프나 대기, 지연상태에 빠지게 해서는 안된다.
			// 실행부는 유저들의 이벤트를 감지한다거나, 프로그램을 운영해야 하므로
			// 무한루프나 대기에 빠지면 본연의 업무를 할 수가 없게 된다.
			// 스마트폰 개발분야에서는 이와같은 코드는 이미 컴파일타임부터 에러발생함. 
			Socket socket = server.accept(); // 무한대기 상태
			area.append("서버 가동..\n");
			//System.out.println(area.getText());
			
			// 클라이언트는 대화를 하기 위해 접속한것이므로, 접속이 되는 순간 스트림을 얻어놓자.
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			
			String data;
			while (true){
				data = buffr.readLine();  // 클라이언트의 메세지 받기
				area.append("클라이언트의 말 : "+data+"\n"); 
				
				buffw.write(data+"\n"); // 클라이언트의 메세지 보내기
				buffw.flush();  // 버퍼비우기
			}
			
		} catch (NumberFormatException e) {
			// 강요하지 않는 예외. RuntimeException
			JOptionPane.showMessageDialog(this, "포트는 숫자로 넣어라");
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
