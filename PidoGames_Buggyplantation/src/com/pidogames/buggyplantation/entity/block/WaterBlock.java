package com.pidogames.buggyplantation.entity.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.Water;

public class WaterBlock extends Block implements Water {
	
	static {
		PHASES_RESID.put(R.drawable.b_water_ud_a1,new int[]{R.drawable.b_water_ud_a1,R.drawable.b_water_ud_a2,R.drawable.b_water_ud_a3,R.drawable.b_water_ud_a4});
		PHASES_RESID.put(R.drawable.b_water_ur_a1,new int[]{R.drawable.b_water_ur_a1,R.drawable.b_water_ur_a2,R.drawable.b_water_ur_a3,R.drawable.b_water_ur_a4});
		PHASES_RESID.put(R.drawable.b_water_ul_a1,new int[]{R.drawable.b_water_ul_a1,R.drawable.b_water_ul_a2,R.drawable.b_water_ul_a3,R.drawable.b_water_ul_a4});
		PHASES_RESID.put(R.drawable.b_water_ocean_a1,new int[]{R.drawable.b_water_ocean_a1,R.drawable.b_water_ocean_a2,R.drawable.b_water_ocean_a3,R.drawable.b_water_ocean_a4});
		PHASES_RESID.put(R.drawable.b_water_mouth_a1,new int[]{R.drawable.b_water_mouth_a1,R.drawable.b_water_mouth_a2,R.drawable.b_water_mouth_a3,R.drawable.b_water_mouth_a4});
	}

	protected WaterBlock(Block b, int x, int y, int level){
		super(b, x, y, level);
	}
	
	public WaterBlock(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	public WaterBlock(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player,type, x, y, level, is_builded);
	}
	
	@Override
	public int getFocusObject(Entity e){
		int mt = e.getMovingType();
		if(mt == MOVING_FLY || mt == MOVING_SWIM || mt==MOVING_FLOAT) return FOCUS_NONE;
		else return FOCUS_BLOCK;
	}
	
	@Override
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = super.getMenuItems();		
		SceneView.getScene().addMenuItemTo(items, R.drawable.menu_rotate,GameMenu.MENU_TF_ACTION, 300);
		return items;
	}
	
	@Override
	public int getDisplayedType(){
		int type = super.getDisplayedType();
		int [] resids = PHASES_RESID.get(type);
		if(resids!=null) return resids[(int)((SceneView.getTic()/4)%resids.length)];
		else return type;
	}
	
	@Override
	public boolean isRotationCached(){
		return true;
	}

	
	@Override
	public int getShape(){
		int shape = 0;
		switch(type){
			case R.drawable.b_water_ud_a1: shape = N4_U; break;
			case R.drawable.b_water_ur_a1: shape = N4_U | N4_R; break;
			case R.drawable.b_water_ul_a1: shape = N4_U | N4_L; break;
			default: return FULL_OVERLAP;
		}
		
		shape <<= ((int)angle/90);
		shape = (shape | (shape >> 4)) & 0xf;
		
		return shape;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_WATER);
		return o;
	}

}
