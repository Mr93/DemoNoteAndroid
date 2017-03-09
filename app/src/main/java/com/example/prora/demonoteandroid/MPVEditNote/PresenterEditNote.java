package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;
import android.util.Log;

import com.example.prora.demonoteandroid.DBHelper;

import java.util.ArrayList;

/**
 * Created by prora on 3/8/2017.
 */

public class PresenterEditNote implements MVP_EditNote.ProvidedPresenter, MVP_EditNote.RequiredPresenter {

	private static final String TAG = PresenterEditNote.class.getSimpleName();
	private MVP_EditNote.RequiredView view;
	private MVP_EditNote.ProvidedModel model;
	private int nodeId = -1;

	public PresenterEditNote(MVP_EditNote.RequiredView view, int nodeId) {
		this.view = view;
		this.nodeId = nodeId;
	}

	@Override
	public void saveNote(String content) {
		Log.d(TAG, "saveNote: " + view);
		if (content.isEmpty()) {
			view.saveError("Note can't empty");
		} else {
			Log.d(TAG, "saveNote: " + content);
			Log.d(TAG, "saveNote: " + nodeId);
			model.saveNoteToDB(content, nodeId);
		}
	}

	@Override
	public void upload(String content) {

	}

	@Override
	public void setView(MVP_EditNote.RequiredView view) {
		this.view = view;
	}

	@Override
	public void setModel(MVP_EditNote.ProvidedModel model) {
		this.model = model;
	}

	@Override
	public Context getContext() {
		return view.getViewContext();
	}

	@Override
	public void noteSaved(int noteId) {
		this.nodeId = noteId;
		view.saveSuccess();
	}

	@Override
	public void noteSaveFailed() {
		view.saveError("Save error");
	}
}
