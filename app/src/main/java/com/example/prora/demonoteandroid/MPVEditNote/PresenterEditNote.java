package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;
import android.util.Log;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;
import com.example.prora.demonoteandroid.SettingsUtils;

import static com.example.prora.demonoteandroid.Constant.KEY_SETTING_ROOT_FILE_DRIVE_ID;

/**
 * Created by prora on 3/8/2017.
 */

public class PresenterEditNote implements MVP_EditNote.ProvidedPresenter, MVP_EditNote.RequiredPresenter {

	//DriveId:CAESABikKCD6maG_1lYoAA==
	private static final String TAG = PresenterEditNote.class.getSimpleName();
	private MVP_EditNote.RequiredView view;
	private MVP_EditNote.ProvidedModel model;

	public PresenterEditNote(MVP_EditNote.RequiredView view) {
		this.view = view;
	}

	@Override
	public void saveNote(Note note) {
		if (note.noteContent.isEmpty()) {
			view.saveError("Note can't empty");
		} else {
			model.saveNoteToDB(note);
		}
	}

	@Override
	public void upload(Note note) {
		if(SettingsUtils.getInstances().getStringSharedPreferences(KEY_SETTING_ROOT_FILE_DRIVE_ID) == ""){
			model.createRootFileInDrive(note);
		}else {
			model.uploadNoteToDrive(note);
		}
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
	public void noteSaved(Note note) {
		view.saveSuccess(note);
	}

	@Override
	public void noteSaveFailed() {
		view.saveError("Save error");
	}
}
