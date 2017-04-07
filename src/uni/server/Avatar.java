/*
 * ���ϰ� ��Ʈ���� ������ 1���� �ξ�����, �����ڸ��� ���ϰ� ��Ʈ���� ���Ѿ� ���� ��Ż���� ��������.
 * ��, ���ϰ� ��Ʈ�� ������ ���� �ʰ� �ִ�.
 * �ذ�å) �� ����ڸ��� �ڽŸ��� ���ϰ� ��Ʈ���� �ʿ��ϹǷ�, 
 *            �����ڸ��� �ν��Ͻ��� �����Ͽ� �� �ν��Ͻ� �ȿ� ��������.
 *            ������ ���ϰ� ��Ʈ������ �����س���
 *            ������ ������ ������ �ؾ� �ϹǷ�, ������� �����Ѵ�.
 */
package uni.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class Avatar extends Thread{
	
	Socket  socket;
	BufferedReader buffr;
	BufferedWriter  buffw;
	JTextArea area;
	
	public Avatar(Socket  socket, JTextArea area) {
		this.socket = socket;
		this.area = area;
		
		// ��ȭ�� ���� ��Ʈ�� �̱�
		try {
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// ���
	public void listen(){
		String msg=null; 
		try {
			msg = buffr.readLine();
			send(msg); // ��ٷ� ������
			area.append(msg+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���ϰ�
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���α׷��� ����ɶ�����, Ŭ���̾�Ʈ�� �޼����� �޾ƿͼ� �ٽ� ������.
	public void run() {
		while (true){
			listen();
		}
	}

}
