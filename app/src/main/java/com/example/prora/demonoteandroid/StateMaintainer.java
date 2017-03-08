package com.example.prora.demonoteandroid;

import com.example.prora.demonoteandroid.MPVEditNote.MVP_EditNote;

import java.util.HashMap;

/**
 * Created by prora on 3/9/2017.
 */

public class StateMaintainer {
	protected final String TAG = getClass().getName();
	private HashMap<Integer, MVP_EditNote.ProvidedPresenter> dataState = new HashMap<>();
	private static StateMaintainer instance;

	private StateMaintainer() {

	}

	public static StateMaintainer getInstance() {
		if (instance == null) {
			instance = new StateMaintainer();
		}
		return instance;
	}

	//creates activity responsible to maintain the objects
	public boolean firstTimeIn(int activityId) {
		try {
			MVP_EditNote.ProvidedPresenter providedPresenterOps = dataState.get(activityId);
			if (providedPresenterOps == null) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void updateStateProvidedPresenterEditNote(int activityId, MVP_EditNote.ProvidedPresenter providedPresenterOps) {
		dataState.put(activityId, providedPresenterOps);
	}

	public MVP_EditNote.ProvidedPresenter getStateProvidedPresenterEditNote(int activityId) {
		return dataState.get(activityId);
	}

}
