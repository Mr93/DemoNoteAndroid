package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.MVP_DisplayNoteList;
import com.example.prora.demonoteandroid.Note;
import com.example.prora.demonoteandroid.SettingsUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.example.prora.demonoteandroid.Constant.KEY_NOTE_CONTENT;
import static com.example.prora.demonoteandroid.Constant.KEY_NOTE_ID;
import static com.example.prora.demonoteandroid.Constant.KEY_SETTING_ROOT_FILE_DRIVE_ID;
import static com.example.prora.demonoteandroid.Constant.REQUEST_CODE_CREATOR;
import static com.example.prora.demonoteandroid.Constant.RESOLVE_CONNECTION_REQUEST_CODE;

/**
 * Created by prora on 3/9/2017.
 */

public class GoogleDriveHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
	private static final String TAG = GoogleDriveHelper.class.getSimpleName();
	private GoogleApiClient googleApiClient;
	private Note noteUpload;
	private Context context;
	private static GoogleDriveHelper instance;
	private GoogleDriveHelper() {

	}

	public synchronized static GoogleDriveHelper getInstance(Context context) {
		if (instance == null) {
			instance = new GoogleDriveHelper();
		}
		instance.context = context;
		return instance;
	}

	public GoogleApiClient getGoogleApiClient(){
		return googleApiClient;
	}

	public synchronized void createApiClient() {
		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(context)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
	}

	public void connectGoogleDrive() {
		Log.d(TAG, "connectGoogleDrive: aaaa");
		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Toast.makeText(context, "Drive connected, try your previous action again", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onConnected: ");
	}



	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended: ");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Activity activity = (Activity) context;
		Log.d(TAG, "onConnectionFailed: aaaa");
		if (activity != null && activity.hasWindowFocus()) {
			if (connectionResult.hasResolution()) {
				try {
					connectionResult.startResolutionForResult((Activity) context, RESOLVE_CONNECTION_REQUEST_CODE);
				} catch (IntentSender.SendIntentException e) {
					e.printStackTrace();
				}
			} else {
				GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), ((Activity) context), 0).show();
			}
		}
	}

	public void createRootFile(Note note) {
		this.noteUpload = note;
		Drive.DriveApi.newDriveContents(googleApiClient)
				.setResultCallback(driveContentsCallback);
	}

	final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
			new ResultCallback<DriveApi.DriveContentsResult>() {
				@Override
				public void onResult(final DriveApi.DriveContentsResult result) {
					new Thread() {
						@Override
						public void run() {
							writeContent();
							IntentSender intentSender = setupIntentSender();
							try {
								((Activity) context).startIntentSenderForResult(
										intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
							} catch (IntentSender.SendIntentException e) {
								Log.w(TAG, "Unable to send intent", e);
							}
						}

						private void writeContent() {
							OutputStream outputStream = result.getDriveContents().getOutputStream();
							Writer writer = new OutputStreamWriter(outputStream);
							try {
								JSONArray jsonArray = new JSONArray();
								JSONObject jsonObject = new JSONObject();
								jsonObject.put(KEY_NOTE_ID, noteUpload.noteId);
								jsonObject.put(KEY_NOTE_CONTENT, noteUpload.noteContent);
								jsonArray.put(jsonObject);
								writer.write(jsonArray.toString());
								writer.close();
							} catch (IOException e) {
								Log.e(TAG, e.getMessage());
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						private IntentSender setupIntentSender() {
							MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
									.setTitle("Backup Note.json")
									.setMimeType("text/plain")
									.setStarred(true).build();
							return Drive.DriveApi
									.newCreateFileActivityBuilder()
									.setInitialMetadata(changeSet)
									.setInitialDriveContents(result.getDriveContents())
									.build(googleApiClient);
						}
					}.start();
				}
			};


	public void uploadNoteToDrive(Note note) {
		this.noteUpload = note;
		DriveId driveId = DriveId.decodeFromString(SettingsUtils.getInstances().getStringSharedPreferences(KEY_SETTING_ROOT_FILE_DRIVE_ID));
		DriveFile driveFile = driveId.asDriveFile();
		new EditContentDrive(context, note).execute(driveFile);
	}

	public void downloadNoteListFromDrive(MVP_DisplayNoteList.ProvidedModel model){
		DriveId driveId = DriveId.decodeFromString(SettingsUtils.getInstances().getStringSharedPreferences(KEY_SETTING_ROOT_FILE_DRIVE_ID));
		DriveFile driveFile = driveId.asDriveFile();
		new GetNoteListFromDrive(context, model).execute(driveFile);
	}

	public String getNoteListJson(DriveFile file){
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

	public boolean handleWriteToDrive(DriveFile file, String stringJson) throws IOException {
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
}
