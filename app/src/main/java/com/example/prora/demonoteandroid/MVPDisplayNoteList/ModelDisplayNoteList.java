package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.os.AsyncTask;
import android.util.Log;

import com.example.prora.demonoteandroid.DBHelper;

import java.util.ArrayList;

/**
 * Created by prora on 3/9/2017.
 */

public class ModelDisplayNoteList implements MVP_DisplayNoteList.ProvidedModel {

	private static final String TAG = ModelDisplayNoteList.class.getSimpleName();
	private MVP_DisplayNoteList.RequiredPresenter presenter;
	private DBHelper dbHelper;

	public ModelDisplayNoteList(MVP_DisplayNoteList.RequiredPresenter presenter) {
		this.presenter = presenter;
		dbHelper = DBHelper.getInstance(presenter.getContext());
	}

	@Override
	public void loadNoteListFromDB() {
		new AsyncTask<Void, Void, ArrayList<Note>>(){
			@Override
			protected ArrayList<Note> doInBackground(Void... params) {
				return dbHelper.getAllNotes();
			}

			@Override
			protected void onPostExecute(ArrayList<Note> values) {
				Log.d(TAG, "onPostExecute: " + values.size());
				if(values != null && !values.isEmpty()){
					presenter.updateDataFromDB(values);
				}
				super.onPostExecute(values);
			}
		}.execute();
	}

}
