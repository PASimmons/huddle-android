package com.huddle.handle.client.resources;

import android.graphics.Bitmap;
import android.view.View;

public class PictureView {
	
	private View picture;
	private Bitmap image;
	public View getPicture() {
		return picture;
	}
	public void setPicture(View picture) {
		this.picture = picture;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}

}
