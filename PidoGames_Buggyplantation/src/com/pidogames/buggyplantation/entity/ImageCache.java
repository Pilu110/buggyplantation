package com.pidogames.buggyplantation.entity;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache extends LruCache<String, Bitmap> {

	public ImageCache(int maxSize) {
		super(maxSize);
	}
	
	@Override
    protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
    }
	
	@Override
	protected void entryRemoved (boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
		//oldValue.recycle();
	}
	
}
