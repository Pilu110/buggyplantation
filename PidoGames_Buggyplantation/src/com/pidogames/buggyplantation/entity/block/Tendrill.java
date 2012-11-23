package com.pidogames.buggyplantation.entity.block;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.item.Item;

public class Tendrill extends PlantBlock {

	public Tendrill(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
	}
	
	public Tendrill(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_TENDRILL);
		return o;
	}
	
	@Override
	public void step(long tic){
		super.step(tic);
		if(tic%5==0 && isSpiky()){
			Map<Item,Boolean> il = SceneView.getScene().getItemsAt(x, y);
			if(il!=null){
				for(Item item : il.keySet()){
					if(getAlignment(item)!=ALIGN_ALLY && item.getMovingType()!=Item.MOVING_FLY){
						item.damage(1, this, player);
					}
				}
			}
		}
	}
	
}
