package pro.chinasoft.activity;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.xmpp.client.util.XmppTool;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddRoomActivity extends Activity implements OnClickListener{

	private String roomName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);

		roomName = getIntent().getStringExtra("USERID");
		TextView tv = (TextView) this.findViewById(R.id.add_f_username);
		tv.setText(roomName);
		Button btn=(Button) this.findViewById(R.id.add_f_submit);
		btn.setOnClickListener(this);

	}
	public void cancel(View v){
		this.finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_friend, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		XMPPConnection connection = XmppTool.getConnection();
		String chatRoomName = "room2@conference.zhangchao";
		
		//加入聊天室(使用昵称喝醉的毛毛虫 ,使用密码ddd)   
		MultiUserChat muc = new MultiUserChat(connection, chatRoomName);   
         
		try {
			muc.join("喝醉的毛毛虫", ""); 
		} catch (XMPPException e) {
			e.printStackTrace();
			Toast.makeText(this, "加入聊天室失败", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this, "加入聊天室成功", Toast.LENGTH_SHORT).show();
	}
}
