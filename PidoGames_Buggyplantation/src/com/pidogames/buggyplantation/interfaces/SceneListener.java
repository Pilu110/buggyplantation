package com.pidogames.buggyplantation.interfaces;

import com.pidogames.buggyplantation.Scene;

public interface SceneListener extends ObjectiveListener {
	public void onChangeBlock(int level, int x, int y);
	public void hookNewSceneReference(Scene scene);
}
