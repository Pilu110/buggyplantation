package com.pidogames.buggyplantation.interfaces;

import com.pidogames.buggyplantation.objective.Objective;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;

public interface ObjectiveListener {
	public void onObjectiveAchieved(Objective objective);
	public void onObjectiveEnabled(Objective objective, boolean enabled);
	public void onMissionCompleted();
	public void onMissionFailed();
	public void onPopupText(String text);
	public void setEvent(ObjectiveEvent event);	
	public void addEventToTriggeredList(ObjectiveEvent event);
}
