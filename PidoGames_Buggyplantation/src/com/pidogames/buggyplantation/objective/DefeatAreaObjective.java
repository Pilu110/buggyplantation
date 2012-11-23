package com.pidogames.buggyplantation.objective;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.graphics.Rect;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;

public class DefeatAreaObjective extends AreaObjective {

	public DefeatAreaObjective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener, Rect area) {
		super(id, scene, player, title, description, achieved_description, enabled, listener, area, false, true);
	}
	
	public DefeatAreaObjective(int id){
		super(id, false, true);
	}

	@Override
	public int achievedAt(int x, int y) {
		
		
		if(typeResId!=NITROGEN_TYPE){
			Map<Item,Boolean> items = scene.getItemsAt(x, y);
			if(items!=null){
				for(Item item : items.keySet()){
					if(Entity.getAlignment(player, item.getPlayer())==Entity.ALIGN_ENEMY && item.getHitPoints()>0 && (typeResId==ALL_TYPE || hasMatchingType(item))) {
						return 0;
					}
				}
			}
		}
		
		for(int level=0; level<scene.getLevels(); level++) {
			Block b = scene.getBlock(level, x, y);
			if(typeResId!=NITROGEN_TYPE){
				if(b!=null && Entity.getAlignment(player, b.getPlayer())==Entity.ALIGN_ENEMY && b.getHitPoints()>0 && (typeResId==ALL_TYPE || hasMatchingType(b))) return 0;
			}
			else {
				if(b!=null && b.isNitrogenBlock()) return 0;
			}
		}
		
		return 1;
	}

	@Override
	public int getType() {
		return OBJECTIVE_TYPE_DEFEAT;
	}

}
