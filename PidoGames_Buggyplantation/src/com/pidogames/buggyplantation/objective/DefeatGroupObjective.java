package com.pidogames.buggyplantation.objective;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;

public class DefeatGroupObjective extends GroupObjective {

	public DefeatGroupObjective(int id) {
		super(id);
	}

	@Override
	public int achievedAt(Entity type) {
		return (type.getHitPoints()<=0)?1:0;
	}

	@Override
	public int getType() {
		return OBJECTIVE_TYPE_DEFEAT_GROUP;
	}

	@Override
	public int getMaxCount() {
		return group.size();
	}

	@Override
	public String getCountSliderTitle(){
		return SceneView.getString(R.string.defeat_to_achieve)+": "+count;
	}

	@Override
	public void setGroupSelectFlag(int flag) {
	}
}
