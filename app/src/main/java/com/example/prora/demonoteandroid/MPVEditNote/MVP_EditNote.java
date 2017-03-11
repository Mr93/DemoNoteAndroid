package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;

import com.example.prora.demonoteandroid.Note;

/**
 * Created by prora on 3/8/2017.
 */

public interface MVP_EditNote {

	interface ProvidedPresenter{
		void saveNote(Note note);
		void upload(Note note);
		void setView(RequiredView view);
		void setModel(ProvidedModel model);
	}

	interface ProvidedModel{
		void saveNoteToDB(Note note);
		void uploadNoteToDrive(Note note);
		void createRootFileInDrive(Note note);
	}

	interface RequiredPresenter{
		Context getContext();
		void noteSaved(Note note);
		void noteSaveFailed();
	}

	interface RequiredView{
		void saveError(String message);
		void saveSuccess(Note note);
		Context getViewContext();
	}

}
