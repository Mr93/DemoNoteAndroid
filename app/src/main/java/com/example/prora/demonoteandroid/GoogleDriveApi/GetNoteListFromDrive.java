package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.prora.demonoteandroid.Constant;
import com.example.prora.demonoteandroid.MVPDisplayNoteList.MVP_DisplayNoteList;
import com.example.prora.demonoteandroid.Note;
import com.google.android.gms.drive.DriveFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by prora on 3/10/2017.
 */

public class GetNoteListFromDrive extends ApiClientAsyncTask<DriveFile, Void, ArrayList<Note>> {

	private static final String TAG = GetNoteListFromDrive.class.getSimpleName();
	private Context context;
	private MVP_DisplayNoteList.ProvidedModel model;

	public GetNoteListFromDrive(Context context, MVP_DisplayNoteList.ProvidedModel model) {
		super(context);
		this.context = context;
		this.model = model;
	}

	@Override
	protected ArrayList<Note> doInBackgroundConnected(DriveFile... params) {
		ArrayList<Note> noteArrayList = new ArrayList<>();
		try {
			DriveFile file = params[0];
			String noteList = GoogleDriveHelper.getInstance(context).getNoteListJson(file);
			Log.d(TAG, "doInBackgroundConnected: " + noteList);
			if (!noteList.equalsIgnoreCase("")) {
				JSONArray jsonArray = new JSONArray(noteList);
				Log.d(TAG, "doInBackgroundConnected: " + jsonArray.toString());
				for (int i = 0; i < jsonArray.length(); i ++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Note note = new Note(jsonObject.getString(Constant.KEY_NOTE_CONTENT), jsonObject.getInt(Constant.KEY_NOTE_ID));
					noteArrayList.add(note);
				}
				Log.d(TAG, "doInBackgroundConnected: " + noteArrayList.isEmpty());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return noteArrayList;
	}

	@Override
	protected void onPostExecute(ArrayList<Note> noteArrayList) {
		if (noteArrayList != null && !noteArrayList.isEmpty()){
			model.noteListFromFetched(noteArrayList);
		}else {
			Toast.makeText(context, "Can't fetch data from drive", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(noteArrayList);
	}
}
