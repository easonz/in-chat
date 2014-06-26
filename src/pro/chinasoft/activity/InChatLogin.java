package pro.chinasoft.activity;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.xmpp.client.util.XmppTool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class InChatLogin extends Activity implements OnClickListener {


	private EditText useridText, pwdText;
	private LinearLayout layout1, layout2;

	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_chat_login);
		SharedPreferences sharedPref = InChatLogin.this
				.getSharedPreferences(
						getString(R.string.in_chat_store),
						Context.MODE_PRIVATE);
		String username=sharedPref.getString(getString(R.string.username_store_key), null);
		String password=sharedPref.getString(getString(R.string.password_store_key), null);
		// ��ȡ�û�������
		this.useridText = (EditText) findViewById(R.id.formlogin_userid);
		this.pwdText = (EditText) findViewById(R.id.formlogin_pwd);
		if(username!=null)
		useridText.setText(username);
		if(password!=null){
			pwdText.setText(password);
		}
		// ���ڵ�¼
		this.layout1 = (LinearLayout) findViewById(R.id.formlogin_layout1);
		// ��¼����
		this.layout2 = (LinearLayout) findViewById(R.id.formlogin_layout2);

		Button btsave = (Button) findViewById(R.id.formlogin_btsubmit);
		btsave.setOnClickListener(this);
		Button btcancel = (Button) findViewById(R.id.formlogin_btcancel);
		btcancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// ����ID�������ύ����ȡ��
		switch (v.getId()) {
		case R.id.formlogin_btsubmit:
			new LoginThread().start();
			break;
		case R.id.formlogin_btcancel:
			finish();
			break;
		}
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
			} else if (msg.what == 2) {
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				Toast.makeText(InChatLogin.this, "��¼ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	public class LoginThread extends Thread {
		// ȡ��������û�������
		final String USERID = useridText.getText().toString();
		final String PWD = pwdText.getText().toString();

		public void run() {
			// sendEmptyMessage:����һ����Ϣ
			handler.sendEmptyMessage(1);
			try {
				// ����
				XMPPConnection connect = XmppTool.getConnection();
				connect.login(USERID, PWD);
				Log.i("XMPPClient", "Logged in as " + XmppTool.getConnection().getUser());

				// ״̬
				Presence presence = new Presence(
						Presence.Type.available);
				XmppTool.getConnection().sendPacket(presence);
				
				// �����û������뵽����
				SharedPreferences sharedPref = InChatLogin.this
						.getSharedPreferences(
								getString(R.string.in_chat_store),
								Context.MODE_PRIVATE);
				
				//���û������뱣��
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(
						getString(R.string.username_store_key), USERID);
				editor.putString(getString(R.string.password_store_key), PWD);
				editor.commit();
				
				Intent intent = new Intent();
				intent.setClass(InChatLogin.this, MainTabActivity.class);
				intent.putExtra("USERID", USERID);
				InChatLogin.this.startActivity(intent);
				InChatLogin.this.finish();
			} catch (XMPPException e) {
				XmppTool.closeConnection();
				handler.sendEmptyMessage(2);
			} catch (Throwable e){
				XmppTool.closeConnection();
				handler.sendEmptyMessage(2);
			}
		}
	}
}