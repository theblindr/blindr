package com.lesgens.blindr;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.models.Server;
import com.lesgens.blindr.models.UserAuthenticatedListener;
import com.todddavies.components.progressbar.ProgressWheel;

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
		Log.i("ConnectFacebookActivity", "onSessionStateChange");
		Controller.getInstance().setSession(session);
		if(state.isOpened() && !connected){
			connected = true;
			session.refreshPermissions();
			List<String> permissions = session.getPermissions();
			Log.i("FACEBOOK_CONNECTION", "Logged in..." + permissions.toString());
			findViewById(R.id.authButton).setVisibility(View.GONE);
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			((ProgressWheel) findViewById(R.id.progressBar)).spin();
			Server.connect(session.getAccessToken());
		} else if(state.isClosed()) {
			connected = false;
			Log.i("FACEBOOK_CONNECTION", "Logged out...");
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.connect_facebook);
		Log.i("ConnectFacebookActivity", "onCreate");
		Server.addUserAuthenticatedListener(this);
		LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
		authButton.setReadPermissions(PERMISSIONS);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("ConnectFacebookActivity", "onActivityResult");
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("ConnectFacebookActivity", "onDestroy");
		uiHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("ConnectFacebookActivity", "onPause");
		uiHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("ConnectFacebookActivity", "onResume");
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("ConnectFacebookActivity", "onSaveInstanceState");
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onUserAuthenticated() {
		Log.i("ConnectFacebookActivity", "onUserAuthenticated");
		Intent i = new Intent(ConnectFacebookActivity.this, PublicChatActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}


}
