package pro.chinasoft.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.xmpp.client.util.XmppTool;

import pro.chinasoft.component.InRoomArrayAdapter;
import pro.chinasoft.component.InUserArrayAdapter;
import pro.chinasoft.model.InRoom;
import pro.chinasoft.model.InUser;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FragmentPage3 extends Fragment {
	private ArrayAdapter<InRoom> rooms = null;
	private Button btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_3, container, false);
		btn = (Button) view.findViewById(R.id.addRoom);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(FragmentPage3.this.getActivity(),
						SearchRoomActivity.class);
				startActivity(intent);
			}

		});
		ListView listView = (ListView) view.findViewById(R.id.fragment_3_list);
		rooms = new InRoomArrayAdapter(getActivity(),	R.layout.in_user_list_item);

		try{
			XMPPConnection connection = XmppTool.getConnection();
			//查找服务    
	        List<String> services = getConferenceServices(connection.getServiceName(), connection);  
	        for (String service : services) {   
	             //查询服务器上的聊天室   
	            Collection<HostedRoom> hrooms = MultiUserChat.getHostedRooms(connection, service);  
	            for(HostedRoom hroom : hrooms) {   
	                //查看Room消息   
	            	InRoom room = new InRoom();
	            	room.setId(hroom.getJid());
	            	room.setName(hroom.getName()); 
	                RoomInfo roomInfo = MultiUserChat.getRoomInfo(connection, hroom.getJid());  
	                if(roomInfo != null) {
	                	room.setDesc(roomInfo.getSubject());
	                    System.out.println(hroom.getName() + ":" + 
	                    		hroom.getJid() + " : " + 
			                	roomInfo.getOccupantsCount() + " : " + 
			                    roomInfo.getSubject());  
	                }
	                rooms.add(room);
	             }     
	        }
		}catch (Exception e){
			e.printStackTrace();
		}

		listView.setAdapter(rooms);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				InRoom room = rooms.getItem(postion);
				Intent intent = new Intent();
				intent.setClass(getActivity(), InRoomChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("roomId", room.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		return view;
	}
	
	public void getChatRooms() throws Exception{
		
		XMPPConnection connection = XmppTool.getConnection();
		
		//查找服务    
        List<String> col = getConferenceServices(connection.getServiceName(), connection);  
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
             }     
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
				}
			}
		}
		return answer;
	}
	
	public void someRoomOprates() throws XMPPException{
		
		XMPPConnection connection = XmppTool.getConnection();
		String chatRoomName = "room1@conference.zhangchao";
		
		//加入聊天室(使用昵称喝醉的毛毛虫 ,使用密码ddd)   
		MultiUserChat muc = new MultiUserChat(connection, chatRoomName);   
        muc.join("喝醉的毛毛虫", "ddd");   
      
        //监听消息   
        muc.addMessageListener(new PacketListener() {
            @Override   
            public void processPacket(Packet packet) {   
                Message message = (Message) packet;   
                System.out.println(message.getFrom() + " : " + message.getBody());;   
            }   
        });     
      
        //并且获取聊天室里最后5条信息，   
        //注：addMessageListener监听器必须在此join方法之前，否则无法监听到需要的5条消息   
        DiscussionHistory history = new DiscussionHistory();   
        history.setMaxStanzas(5);   
        muc.join("喝醉的毛毛虫", "ddd", history, SmackConfiguration.getPacketReplyTimeout());   
      
        //监听拒绝加入聊天室的用户   
        muc.addInvitationRejectionListener(new InvitationRejectionListener() {   
            @Override   
            public void invitationDeclined(String invitee, String reason) {   
            	System.out.println(invitee + " reject invitation, reason is " + reason);   
            }   
        });   
        
        //邀请用户加入聊天室   
        muc.invite("2@zhangchao", "大家来谈谈人生");  
        
        //监听邀请加入聊天室请求   
        MultiUserChat.addInvitationListener(connection, new InvitationListener() {  
        	@Override
            public void invitationReceived(Connection conn, String room, String inviter, String reason, String password, Message message) {   
	            //例：直接拒绝邀请   
	            MultiUserChat.decline(conn, room, inviter, "你丫很闲啊！");   
            } 
        });   
      
        //根据roomJID获取聊天室信息   
        RoomInfo roomInfo = MultiUserChat.getRoomInfo(connection, chatRoomName);  
        System.out.println(roomInfo.getRoom() + "-" + roomInfo.getSubject() + "-" + roomInfo.getOccupantsCount());  
           
        //判断用户是否支持Multi-User聊天协议   
        //注：需要加上资源标识符   
//        boolean supports = MultiUserChat.isServiceEnabled(connection, "2@zhangchao/spark");  
//         //获取某用户所加入的聊天室   
//        if(supports) {   
//            Iterator<String> joinedRooms = MultiUserChat.getJoinedRooms(connection, "2@zhangchao/spark");  
//            while(joinedRooms.hasNext()) {   
//                System.out.println("2@zhangchao has joined Room " + joinedRooms.next());   
//            }   
//        }   
	}
}