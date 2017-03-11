package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.os.AsyncTask;
import android.util.Log;

import com.example.prora.demonoteandroid.DBHelper;
import com.example.prora.demonoteandroid.GoogleDriveApi.GoogleDriveHelper;

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
				if(values != null && !values.isEmpty()){
					presenter.updateDataFromDB(values);
				}
				super.onPostExecute(values);
			}
		}.execute();
	}

	@Override
	public void deleteNoteFromDB(final int noteId) {
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				dbHelper.deleteNote(noteId);
				return null;
			}
		}.execute();
	}

	@Override
	public void fetchNoteListFromDrive() {
		GoogleDriveHelper.getInstance(presenter.getContext()).downloadNoteListFromDrive(this);
	}

	@Override
	public void noteListFromFetched(final ArrayList<Note> noteContentList) {
		new AsyncTask<Void, Void, ArrayList<Note>>(){
			@Override
			protected ArrayList<Note> doInBackground(Void... params) {
				ArrayList<Note> localNoteList = dbHelper.getAllNotes();
				syncLocalAndDrive(localNoteList, noteContentList);
				return dbHelper.getAllNotes();
			}

			@Override
			protected void onPostExecute(ArrayList<Note> values) {
				if(values != null && !values.isEmpty()){
					presenter.updateDataFromDB(values);
				}
				super.onPostExecute(values);
			}
		}.execute();
	}

	private void syncLocalAndDrive(ArrayList<Note> localNoteList, ArrayList<Note> noteContentList) {
		for (int i = 0; i < noteContentList.size(); i++){
			boolean sameId = false;
			for (int j = 0; j < localNoteList.size(); j++){
				if(noteContentList.get(i).noteId == localNoteList.get(j).noteId){
					sameId = true;
					break;
				}
			}
			if(sameId){
				dbHelper.updateNote(noteContentList.get(i).noteContent, noteContentList.get(i).noteId);
			}else {
				Log.d(TAG, "doInBackground: " + dbHelper.insertNoteWithId(noteContentList.get(i).noteContent,
						noteContentList.get(i).noteId));
			}
		}
	}

}
