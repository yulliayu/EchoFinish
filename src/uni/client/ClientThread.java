/*
 * 실시간 청취를 위해, 메인쓰레드가 아닌 개발자 정의 쓰레드를 루프로 돌리자.
 */
package uni.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	
	Socket  socket;
	BufferedReader  buffr;
	BufferedWriter   buffw;
	JTextArea  area;
	
	public ClientThread(Socket  socket, JTextArea  area) {
		this.socket = socket;
		this.area = area;
		
		try {
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 보내기 (말하기)
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 받기 (듣기)
	public void listen(){
		
	}
	
	public void run() {
		
	}
}
