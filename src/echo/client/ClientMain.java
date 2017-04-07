package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	
	JPanel p_north;
	Choice  choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JScrollPane scroll;
	JTextArea  area;
	int port = 7777;
	DBManager  manager;
	ArrayList<Chat> list=new ArrayList<Chat>();
	Socket  socket; // 대화용 소켓. 따라서 스트림도 뽑아낼거임.
	String ip;
	ClientThread ct;
	
	public ClientMain() {
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port), 10);
		bt_connect = new JButton("접속");
		area = new JTextArea(15,15);
		scroll = new JScrollPane(area);
		t_input = new JTextField(15);
		
		manager = DBManager.getInstance();
		
		choice.setPreferredSize(new Dimension(70, 10));
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(t_input, BorderLayout.SOUTH);
		
		// 데이터 가져오기 
		loadIP();
		
		// choice 에 넣기
		for (int i=0; i<list.size();i++){
			choice.add(list.get(i).getName());
		}
		
		// 리스너 연결
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (key==KeyEvent.VK_ENTER){
					String msg=t_input.getText();
					
					// 보내기
					ct.send(msg);
					
					// 입력한 글씨 지우기
					t_input.setText("");
				}
			}
		});
		
		setVisible(true);
		setBounds(300, 300, 300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	// 데이터베이스 가져오기
	public void loadIP(){
		Connection con = manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql ="select * from chat order by chat_id ";

		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			// rs 의 모든 데이터를 dto 로 옮기는 과정.
			while(rs.next()){
				Chat dto = new Chat();
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (pstmt!=null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			manager.disConnect(con);
		}
		//System.out.println("list.size = "+list.size());
	}

	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index = ch.getSelectedIndex();
		Chat chat = list.get(index);
		this.setTitle(chat.getIp());
		ip = chat.getIp(); // 멤버변수에도 대입
	}
	
	// 서버의 접속을 시도한다.
	public void connect(){
		// 소켓 생성시 접속이 발생함.
		try {
			port = Integer.parseInt(t_port.getText());
			socket = new Socket(ip, port);
			
			// 실시간으로 서버의 메세지를 청취하기 위해
			// 쓰레드를 생성하여 대화업무를 맡겨버리자.
			// 따라서 종이컵&실 의 보유자는 동생.
			ct = new ClientThread(socket, area);
			ct.start();
			
			ct.send("안녕?");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		connect();
	}

	public static void main(String[] args) {
		new ClientMain();
	}

}
