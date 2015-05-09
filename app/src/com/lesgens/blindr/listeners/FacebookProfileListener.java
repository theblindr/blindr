package com.lesgens.blindr.listeners;

import java.util.List;

public interface FacebookProfileListener {

	public void onSlideshowPicturesReceived(List<String> pictures);
	
	public void onProfilePicturesReceived(List<String> pictures);
}
