package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by prora on 3/9/2017.
 */

public interface MVP_DisplayNoteList {

	interface ProvidedPresenter{
		void initRecyclerView();
		void setModel(MVP_DisplayNoteList.ProvidedModel model);
		void setView(MVP_DisplayNoteList.RequiredView view);
		void getNoteListFromDrive();
	}

	interface ProvidedModel{
		void loadNoteListFromDB();
		void deleteNoteFromDB(int noteId);
		void fetchNoteListFromDrive();
		void noteListFromFetched(ArrayList<Note> noteContentList);
	}

	interface RequiredPresenter{
		Context getContext();
		void updateDataFromDB(ArrayList<Note> noteContentList);
	}

	interface RequiredView{
		RecyclerView getRecyclerView();
		Context getViewContext();
	}
}
