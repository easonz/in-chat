package pro.chinasoft.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
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
import org.xmpp.client.util.InMessageStore;
import org.xmpp.client.util.XmppTool;

import pro.chinasoft.component.InUserArrayAdapter;
import pro.chinasoft.model.InMessage;
import pro.chinasoft.model.InUser;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentPage1 extends Fragment {

	private ArrayAdapter<InUser> friends = null;
	private List<InMessage> msgs = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_1, container, false);
		ListView listView = (ListView) view.findViewById(R.id.fragment_1_list);
		friends = new InUserArrayAdapter(getActivity(), R.layout.in_user_list_item);
		msgs = InMessageStore.getUserMessage(this.getActivity());

		for (int i = 0; i < msgs.size(); i++) {
			InUser iu = msgs.get(i).getInUser();
			friends.add(iu);
		}
		
		listView.setAdapter(friends);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				InUser user = friends.getItem(postion);
				if("".equals(user.getUserId()) || null == user.getUserId()){
					//群消息或者广播消息暂不支持
					Toast.makeText(getActivity(), "群消息或者广播消息暂不支持", Toast.LENGTH_SHORT).show();
				}
				else{
					Intent intent = new Intent();
					intent.setClass(getActivity(), InChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("userid", user.getUserId());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
}
