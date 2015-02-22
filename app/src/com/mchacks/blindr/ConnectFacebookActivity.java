package com.mchacks.blindr;

import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.Server;
import com.mchacks.blindr.models.UserAuthenticatedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class ConnectFacebookActivity extends Activity implements UserAuthenticatedListener{

	private static final String[] PERMISSIONS = {"public_profile", "user_photos"};
	private boolean connected = false;
	
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	}; 
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Controller.getInstance().setSession(session);
		if(state.isOpened() && !connected){
			connected = true;
			session.refreshPermissions();
			List<String> permissions = session.getPermissions();
			Log.i("FACEBOOK_CONNECTION", "Logged in..." + permissions.toString());
			Server.connect(session.getAccessToken());
		} else if(state.isClosed()) {
			connected = false;
			Log.i("FACEBOOK_CONNECTION", "Logged out...");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Server.addUserAuthenticatedListener(this);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.connect_facebook);
		
		LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
		authButton.setReadPermissions(PERMISSIONS);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if(Controller.getInstance().getSession() == null) {
//			Session session = Session.getActiveSession();
//			if(session != null && 
//					(session.isOpened() || session.isClosed())) {
//				onSessionStateChange(session, session.getState(), null);
//			}
//		}
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onUserAuthenticated() {
		Intent i = new Intent(ConnectFacebookActivity.this, PublicChatActivity.class);
		startActivity(i);
		finish();
	}
	
	
}
