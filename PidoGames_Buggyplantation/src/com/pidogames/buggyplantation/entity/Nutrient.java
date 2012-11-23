package com.pidogames.buggyplantation.entity;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.interfaces.Constants;

public class Nutrient extends Entity implements Constants {
	
	private int icon_resid;
	
	public Nutrient(int icon_resid) {
		super(PLAYER_NEUTRAL);
		this.icon_resid = icon_resid;
	}

	@Override
	public int getMaxHitPoints() {
		return 0;
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
		return LEAF_LEVEL;
	}

	@Override
	public int getType(){
		return icon_resid;
	}
	
	@Override
	public String getTypeName(){
		return ResIdConverter.getNameForResid(icon_resid);
	}
	
	@Override
	protected String getBaseHashKey() {
		return "N_"+icon_resid;
	}

	@Override
	protected String getHashKey() {
		return "N_"+icon_resid;
	}

	@Override
	protected Bitmap loadBitmap() {
		return loadBitmapResource(icon_resid);
	}

	@Override
	public boolean isRotationCached() {
		return false;
	}

	@Override
	public void step(long tic) {
	}

}
