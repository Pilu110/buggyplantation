package com.pidogames.buggyplantation.entity.block;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.bug.Bug;

public class Bait extends PlantBlock {

	protected Coord position;
	private boolean is_active;
	
	protected static final String JSON_IS_ACTIVE = registerJSONKey("c", Bait.class);	
	
	public Bait(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		position = getPosition();
		if(!o.isNull(JSON_IS_ACTIVE)) is_active = o.getBoolean(JSON_IS_ACTIVE);
		else is_active = true;
	}
	
	public Bait(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
		position = getPosition();
		is_active = true;
	}
	
	public void setActive(boolean is_active){
		this.is_active = is_active;
	}
	
	public int getSqrRange(){
		return 120*120;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		if(is_active && tic%10==0){
			List<Item> bugs = SceneView.getScene().getItems(BUG_LEVEL);
			if(bugs!=null){
				int sqr_range = getSqrRange();
				for(Item item : bugs){
					if(item instanceof Bug && getAlignment(item)!=ALIGN_ALLY){
						Bug b = (Bug)item;						
						if(position.d2(b.getCoord())<sqr_range) b.setDestination(this, true, 0, 0, false, true, null);
						//if(position.d2(b.getCoord())<(b.getSmellDistance()*b.getSmellDistance())) b.addFoodMemory(b_position);
					}
				}
			}
		}
	}

	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		if (!is_active) o.put(JSON_IS_ACTIVE, is_active);
		o.put(JSON_INSTANCEOF, JSON_CLASS_BAIT);
		return o;
	}
}
