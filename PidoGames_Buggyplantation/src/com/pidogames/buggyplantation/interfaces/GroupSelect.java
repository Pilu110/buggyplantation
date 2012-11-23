package com.pidogames.buggyplantation.interfaces;

import java.util.HashSet;

import com.pidogames.buggyplantation.entity.Entity;

public interface GroupSelect {
	public HashSet<Entity> getTargets();
	public void toggleTarget(Entity e);
	public void setGroupSelectFlag(int flag);
	public String getDisplayableTitle();
}
