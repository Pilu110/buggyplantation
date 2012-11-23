package com.pidogames.buggyplantation.objective;

public class StartUpObjective extends Objective {

	public StartUpObjective(int id) {
		super(id);
	}

	@Override
	public int getType() {
		return OBJECTIVE_TYPE_STARTUP;
	}

	@Override
	protected boolean checkIfAchieved(long tic) {
		return true;
	}

}
