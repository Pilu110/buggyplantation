package com.pidogames.buggyplantation.interfaces;

import com.pidogames.buggyplantation.MenuItem;

public interface MenuListener {
	public void onMenuItemSelected(MenuItem mi);
	public void onMenuItemReady(MenuItem mi);
	public void onSelectMenuLevel(int level);
	public void closeMenu();
	public void displayMainMenu();
}
