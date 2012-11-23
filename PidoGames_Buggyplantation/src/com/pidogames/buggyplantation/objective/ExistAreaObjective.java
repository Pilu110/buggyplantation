package com.pidogames.buggyplantation.objective;

import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Rect;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;

public class ExistAreaObjective extends AreaObjective implements DisplayableState {
	
	
	public ExistAreaObjective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener, Rect area) {
		super(id, scene, player, title, description, achieved_description, enabled, listener, area, true, true);
	}
	
	public ExistAreaObjective(int id){
		super(id, true, true);
	}
	
	@Override
	public int achievedAt(int x, int y) {
		
		
		if(typeResId!=NITROGEN_TYPE){
			Map<Item,Boolean> items = scene.getItemsAt(x, y);
			if(items!=null){
				int cnt = 0;
				
				for(Item item : items.keySet()){
					if(item.getPlayer()==player && (typeResId==ALL_TYPE || hasMatchingType(item)) && !item_counted.contains(item)) {
						cnt++;
						item_counted.add(item);
					}
				}
				
				if(cnt>0) return cnt;
			}
		}
		
		for(int level=0; level<scene.getLevels(); level++) {
			Block b = scene.getBlock(level, x, y);
			if(typeResId!=NITROGEN_TYPE){
				if(b!=null && b.getPlayer()==player && b.isBuilded() && (typeResId==ALL_TYPE || hasMatchingType(b))) return 1;
			}
			else {
				if(b!=null && b.isNitrogenBlock()) return 1;
			}
		}
		
		return 0;
	}
	
	@Override
	public int getType() {
		return OBJECTIVE_TYPE_EXIST;
	}

	@Override
	public String getDisplayableState() {
		return sum+" / "+count;
	}
	
}
