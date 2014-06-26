package pro.chinasoft.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.xmpp.client.util.XmppTool;

import pro.chinasoft.dialog.LoadingDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

public class SearchRoomActivity extends Activity {
	
	LoadingDialog dialog; 
	// loading dialog==========================================
	// define variable
	public ProgressDialog pd;
	// define Handler Ojbect
	final Handler handler = new Handler() {
		@Override
		// execute the method of handler,when message came.
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog.cancel();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_friend_activity);
	}

	public void search(View v) {
		//loading dialog
	    dialog = new LoadingDialog(this);  
        dialog.setCanceledOnTouchOutside(false);  
        dialog.show();
		EditText txt = (EditText) SearchRoomActivity.this
				.findViewById(R.id.search_user_name);
		String keyword = txt.getText().toString();
		SearchThread t = new SearchThread();
		t.setKeyword(keyword);
		t.start();
	}
	
	public void cancel(View v){
		this.finish();
	}
	
	public void finish(){
		dialog.cancel();
		super.finish();
	}
	
	class SearchThread extends Thread{
		private String keyword;
		@Override
		public void run() {
			// here execute processions take long time.
			try {
				XMPPConnection connection = XmppTool.getConnection();
				
				//查找服务    
		        List<String> col = getConferenceServices(connection.getServiceName(), connection); 
		        boolean fond = false;
		        for (Object aCol : col) {   
		            String service = (String) aCol;   
		             //查询服务器上的聊天室   
		            Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(connection, service);  
		            for(HostedRoom room : rooms) {   
		                //查看Room消息   
		                System.out.println(room.getName() + " - " +room.getJid());   
		                RoomInfo roomInfo = MultiUserChat.getRoomInfo(connection, room.getJid());  
		                if(roomInfo != null) {   
		                    System.out.println(roomInfo.getOccupantsCount() + " : " + roomInfo.getSubject());  
		                }
		                if(keyword.equals(room.getName())){
		                	fond = true;
		                }
		             }     
		        } 
		        
		        if(fond){
					Intent intent = new Intent();
					intent.setClass(SearchRoomActivity.this, AddRoomActivity.class);
					intent.putExtra("roomName", keyword);
					SearchRoomActivity.this.startActivity(intent);
					SearchRoomActivity.this.finish();
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
	}
	
	public static List<String> getConferenceServices(String server,	XMPPConnection connection) throws Exception {
		System.out.println("服务器名称：" + server);
		List<String> answer = new ArrayList<String>();
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
		DiscoverItems items = discoManager.discoverItems(server);
		for (Iterator<DiscoverItems.Item> it = items.getItems(); it.hasNext();) {
			DiscoverItems.Item item = (DiscoverItems.Item) it.next();
			if (item.getEntityID().startsWith("conference")
					|| item.getEntityID().startsWith("private")) {
				answer.add(item.getEntityID());
			} else {
				try {
					DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
					if (info.containsFeature("http://jabber.org/protocol/muc")) {
						answer.add(item.getEntityID());
					}
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		}
		return answer;
	}
}
