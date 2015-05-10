package com.lesgens.blindr.listeners;

public interface UserAuthenticatedListener {
	public void onUserAuthenticated();
	public void onUserNetworkErrorAuthentication();
	public void onUserServerErrorAuthentication();
}