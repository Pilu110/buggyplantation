package com.pidogames.buggyplantation.entity;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;

public class FlowerImage extends Entity {

	private static final int [] ANIM_PHASES = {
		R.drawable.b_flower_a5,
		R.drawable.b_flower_a4,
		R.drawable.b_flower_a3,
		R.drawable.b_flower_a2,
		R.drawable.b_flower
	};
		
	private int anim_phase;
	
	public FlowerImage() {
		super(Entity.PLAYER_NEUTRAL);
		anim_phase = 0;
	}

	public void setAnimPhase(int phase){
		anim_phase = (phase<ANIM_PHASES.length)?phase:(ANIM_PHASES.length-1);
	}
	
	public void setAnimPhaseByRate(float rate){
		this.anim_phase = rate<1?((int)(ANIM_PHASES.length*rate)):(ANIM_PHASES.length-1);
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
	public int getType() {
		return 0;
	}

	@Override
	public String getTypeName() {
		return null;
	}

	@Override
	protected String getBaseHashKey() {
		return "FL_"+ANIM_PHASES[anim_phase]+"_0";
	}

	@Override
	protected String getHashKey() {
		int scaled_angle = (int)(2.0*angle) / 45;
		return "FL_"+ANIM_PHASES[anim_phase]+"_"+scaled_angle;
	}

	@Override
	protected Bitmap loadBitmap() {
		return loadBitmapResource(ANIM_PHASES[anim_phase]);
	}

	@Override
	public boolean isRotationCached() {
		return true;
	}

	@Override
	public void step(long tic) {
	}

}
