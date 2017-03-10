package com.example.prora.demonoteandroid.MPVEditNote;

import android.os.AsyncTask;

import com.example.prora.demonoteandroid.DBHelper;
import com.example.prora.demonoteandroid.GoogleDriveApi.GoogleDriveHelper;
import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;

/**
 * Created by prora on 3/8/2017.
 */

public class ModelEditNote implements MVP_EditNote.ProvidedModel {

	private static final String TAG = ModelEditNote.class.getSimpleName();
	private MVP_EditNote.RequiredPresenter presenter;
	private DBHelper dbHelper;
	public static final int ID_ERROR = -1;

	public ModelEditNote(MVP_EditNote.RequiredPresenter presenter) {
		this.presenter = presenter;
		dbHelper = DBHelper.getInstance(presenter.getContext());
	}

	@Override
	public void saveNoteToDB(final Note note) {
		new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				if(note.noteId == ID_ERROR){
					return dbHelper.insertNote(note.noteContent);
				}else {
					return dbHelper.updateNote(note.noteContent, note.noteId);
				}
			}

			@Override
			protected void onPostExecute(Integer value) {
				if(value != ID_ERROR){
					note.noteId = value;
					presenter.noteSaved(note);
				}else {
					presenter.noteSaveFailed();
				}
				super.onPostExecute(value);
			}
		}.execute();
	}

	@Override
	public void uploadNoteToDrive(Note note) {

	}

	@Override
	public void createRootFileInDrive(Note note) {
		GoogleDriveHelper.getInstance().createRootFile(presenter.getContext(), note);
	}


}
