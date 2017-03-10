package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;


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
			String noteList = getNoteListJson(file);
			if (noteList.equalsIgnoreCase("")){
				return false;
			}else {
				JSONObject jsonObject = new JSONObject(noteList);
				jsonObject.put(String.valueOf(noteUpload.noteId), noteUpload.noteContent);
				Log.d(TAG, "doInBackgroundConnected: " + noteList);
				Log.d(TAG, "doInBackgroundConnected: " + jsonObject.toString());
				return handleWriteToDrive(file, jsonObject.toString());
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException while appending to the output stream", e);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String getNoteListJson(DriveFile file){
		String contentsAsString = "";
		try {
			DriveApi.DriveContentsResult driveContentsResult = file.open(
					getGoogleApiClient(), DriveFile.MODE_READ_ONLY , null).await();
			if (!driveContentsResult.getStatus().isSuccess()) {
				return contentsAsString;
			}
			DriveContents contents = driveContentsResult.getDriveContents();
			BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			 contentsAsString = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentsAsString;
	}

	private boolean handleWriteToDrive(DriveFile file, String stringJson) throws IOException {
		DriveApi.DriveContentsResult driveContentsResult = file.open(
				getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
		if (!driveContentsResult.getStatus().isSuccess()) {
			return false;
		}
		DriveContents driveContents = driveContentsResult.getDriveContents();
		OutputStream outputStream = driveContents.getOutputStream();
		outputStream.write(stringJson.getBytes());
		com.google.android.gms.common.api.Status status =
				driveContents.commit(getGoogleApiClient(), null).await();
		return status.getStatus().isSuccess();
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
