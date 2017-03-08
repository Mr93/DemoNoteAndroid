package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;

/**
 * Created by prora on 3/8/2017.
 */

public interface MVP_EditNote {

	interface ProvidedPresenter{
		void saveNote(String content);
		void upload(String content);
		void setView(RequiredView view);
		void setModel(ProvidedModel model);
	}

	interface ProvidedModel{
		void saveNoteToDB(String content, int noteId);
	}

	interface RequiredPresenter{
		Context getContext();
		void noteSaved(int noteId);
		void noteSaveFailed();
	}

	interface RequiredView{
		void saveError(String message);
		void saveSuccess();
		Context getViewContext();
	}

}
