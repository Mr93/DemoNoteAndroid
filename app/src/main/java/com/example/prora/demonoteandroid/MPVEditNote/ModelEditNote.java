package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.prora.demonoteandroid.DBHelper;

/**
 * Created by prora on 3/8/2017.
 */

public class ModelEditNote implements MVP_EditNote.ProvidedModel {

	private static final String TAG = ModelEditNote.class.getSimpleName();
	private MVP_EditNote.RequiredPresenter presenter;
	private DBHelper dbHelper;

	public ModelEditNote(MVP_EditNote.RequiredPresenter presenter) {
		this.presenter = presenter;
		dbHelper = DBHelper.getInstance(presenter.getContext());
	}

	@Override
	public void saveNoteToDB(final String content, final int noteId) {
		new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				if(noteId == -1){
					return dbHelper.insertNote(content);
				}else {
					return dbHelper.updateNote(content, noteId);
				}
			}

			@Override
			protected void onPostExecute(Integer value) {
				if(value != -1){
					presenter.noteSaved(value);
				}else {
					presenter.noteSaveFailed();
				}
				super.onPostExecute(value);
			}
		}.execute();
	}
}
