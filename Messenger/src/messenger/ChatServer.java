package messenger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatServer {
	static Vector ClientSockets;//this will allow us to create many  clients and many login names for each of our clients that connects
	static Vector LoginNames;
	
	ChatServer() throws IOException{//we have to throw an exception in case this fails. ovo je konstruktor
		ServerSocket server=new ServerSocket(5217);
		ClientSockets= new Vector();
		LoginNames=new Vector();
		
		while(true) {          //endless while loop
			Socket client=server.accept();
			AcceptClient acceptClient=new AcceptClient(client);
			
		}
	}
	public static void main(String[] args) throws IOException {
		ChatServer server= new ChatServer();
	}
	class AcceptClient extends Thread{
		Socket ClientSocket;
		DataInputStream din;
		DataOutputStream dout;
		AcceptClient(Socket client) throws IOException{//constructor that accept socket
			ClientSocket=client;
			din=new DataInputStream(ClientSocket.getInputStream());
			dout=new DataOutputStream(ClientSocket.getOutputStream());
			
			String LoginName=din.readUTF();
			
			LoginNames.add(LoginName);
			ClientSockets.add(ClientSocket);
			
			start();
		}
		public void run() {
			while(true) {
				try {
				String msgFromClient=din.readUTF();
				StringTokenizer st= new StringTokenizer(msgFromClient);
				String LoginName=st.nextToken();
				String MsgType=st.nextToken();
				int lo=-1;
				
				String msg=" ";
				
				while(st.hasMoreTokens()) {
					msg=msg+" "+ st.nextToken();
				}
				
				if(MsgType.equals("LOGIN")) {
				for(int i=0;i<LoginNames.size();i++) {
					Socket pSocket=(Socket) ClientSockets.elementAt(i);                                       //u zagradi socket cast socket from vector
					DataOutputStream pOut=new DataOutputStream(pSocket.getOutputStream());
					pOut.writeUTF(LoginName+ " has logged in.");
					}
				}
				else if(MsgType.equals("LOGOUT")) {
					for(int i=0;i<LoginNames.size();i++) {
						if(LoginName.equals(LoginNames.elementAt(i)))
							lo=i;
						Socket pSocket=(Socket) ClientSockets.elementAt(i);                                       //u zagradi socket cast socket from vector
						DataOutputStream pOut=new DataOutputStream(pSocket.getOutputStream());
						pOut.writeUTF(LoginName+ " has logged out.");
						}
					if(lo>=0) {
						LoginNames.removeElementAt(lo);
						ClientSockets.removeElementAt(lo);
					}
					}
				else {
						for(int i=0;i<LoginNames.size();i++) {
							Socket pSocket=(Socket) ClientSockets.elementAt(i);                                       //u zagradi socket cast socket from vector
							DataOutputStream pOut=new DataOutputStream(pSocket.getOutputStream());
							pOut.writeUTF(LoginName+ ": " +msg);
							}
				}
				if(MsgType.equals("LOGOUT"))
					break;
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
