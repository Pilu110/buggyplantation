package com.pidogames.buggyplantation.entity;

import org.json.JSONObject;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BackgroundPattern extends Entity implements Constants {
	
	public BackgroundPattern(){
		super(0);
	}

	@Override
	public int getMaxHitPoints() {
		return 0;
	}

	@Override
	protected String getBaseHashKey() {
		return "BG_0";
	}

	@Override
	protected String getHashKey() {
		return "BG_"+angle;
	}

	@Override
	protected Bitmap loadBitmap() {
		return loadBitmapResource(R.drawable.background_pattern);
	}

	@Override
	public boolean isRotationCached() {
		return true;
	}

	@Override
	public int getType(){
		return R.drawable.background_pattern;
	}
	
	@Override
	public String getTypeName(){
		return null;
	}
	
	@Override
	public void step(long tic) {
	}

	@Override
	public boolean hasMenu() {
		return false;
	}

	@Override
	public GameMenu getMenu() {
		return null;
	}
	
	@Override
	public void initMenuFromJSON(JSONObject o, ResIdConverter rc) {
	}

	@Override
	public int getLevel() {
		return AIR_LEVEL;
	}
}
