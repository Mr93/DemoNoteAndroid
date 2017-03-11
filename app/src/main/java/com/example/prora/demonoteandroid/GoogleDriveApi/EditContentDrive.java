package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static com.example.prora.demonoteandroid.Constant.KEY_NOTE_CONTENT;
import static com.example.prora.demonoteandroid.Constant.KEY_NOTE_ID;


/**
 * Created by prora on 3/10/2017.
 */

public class EditContentDrive extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

	private static final String TAG = EditContentDrive.class.getSimpleName();
	private Note noteUpload;
	private Context context;

	public EditContentDrive(Context context, Note note) {
		super(context);
		this.context = context;
		this.noteUpload = note;
	}

	@Override
	protected Boolean doInBackgroundConnected(DriveFile... params) {
		DriveFile file = params[0];
		try {
			String noteList = GoogleDriveHelper.getInstance(context).getNoteListJson(file);
			if (noteList.equalsIgnoreCase("")){
				return false;
			}else {
				JSONArray jsonArray = new JSONArray(noteList);
				boolean isNewNote = true;
				isNewNote = handleOldNote(jsonArray, isNewNote);
				if(isNewNote){
					handleNewNote(jsonArray);
				}
				Log.d(TAG, "doInBackgroundConnected: " + jsonArray.toString());
				return GoogleDriveHelper.getInstance(context).handleWriteToDrive(file, jsonArray.toString());
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException while appending to the output stream", e);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean handleOldNote(JSONArray jsonArray, boolean isNewNote) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++){
			if(jsonArray.getJSONObject(i).getInt(KEY_NOTE_ID) == noteUpload.noteId){
				jsonArray.getJSONObject(i).put(KEY_NOTE_CONTENT, noteUpload.noteContent);
				isNewNote = false;
			}
		}
		return isNewNote;
	}

	private void handleNewNote(JSONArray jsonArray) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(KEY_NOTE_ID, noteUpload.noteId);
		jsonObject.put(KEY_NOTE_CONTENT, noteUpload.noteContent);
		jsonArray.put(jsonObject);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (!result) {
			Log.d(TAG, "onPostExecute: fail " + result);
			Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "Upload success", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "onPostExecute: true " + result);
		}
	}
}
