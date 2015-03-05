package com.lesgens.blindr.models;

import java.util.List;

public interface FacebookProfileListener {

	public void onProfilePicturesReceived(List<String> pictures);
}
