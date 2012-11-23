package com.pidogames.buggyplantation.objective.event;

import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.objective.Objective;

public class VictoryEvent extends ObjectiveEvent {
	
	public VictoryEvent(int id, Objective parent) {
		super(id, parent);
	}
	
	@Override
	public void triggerFunction(ObjectiveListener listener) {
		if(listener!=null) listener.onMissionCompleted();
		fulfill();
	}
	
	@Override
	public int getType(){
		return OBJECTIVE_EVENT_VICTORY;
	}
}
