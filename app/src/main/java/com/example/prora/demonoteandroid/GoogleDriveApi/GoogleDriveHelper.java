package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.example.prora.demonoteandroid.Constant.REQUEST_CODE_CREATOR;
import static com.example.prora.demonoteandroid.Constant.RESOLVE_CONNECTION_REQUEST_CODE;

/**
 * Created by prora on 3/9/2017.
 */

public class GoogleDriveHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = GoogleDriveHelper.class.getSimpleName();
	private GoogleApiClient googleApiClient;
	private Note noteUpload;
	private Context context;

	private static GoogleDriveHelper instance;

	private GoogleDriveHelper() {

	}

	public synchronized static GoogleDriveHelper getInstance() {
		if (instance == null) {
			instance = new GoogleDriveHelper();
		}
		return instance;
	}

	public synchronized void createApiClient(Context context) {
		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(context)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
	}

	private void connectGoogleDrive(Context context) {
		this.context = context;
		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Toast.makeText(context, "Connected: click upload/download again", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onConnected: ");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended: ");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Activity activity = (Activity) context;
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

	public void createRootFile(Context context, Note note) {
		if (!googleApiClient.isConnected()) {
			connectGoogleDrive(context);
		} else {
			this.noteUpload = note;
			Drive.DriveApi.newDriveContents(googleApiClient)
					.setResultCallback(driveContentsCallback);
		}
	}

	final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
			new ResultCallback<DriveApi.DriveContentsResult>() {

				@Override
				public void onResult(final DriveApi.DriveContentsResult result) {
					new Thread() {
						@Override
						public void run() {
							// write content to DriveContents
							OutputStream outputStream = result.getDriveContents().getOutputStream();
							Writer writer = new OutputStreamWriter(outputStream);
							try {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put(String.valueOf(noteUpload.noteId), noteUpload.noteContent);
								writer.write(jsonObject.toString());
								writer.close();
							} catch (IOException e) {
								Log.e(TAG, e.getMessage());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
									.setTitle("Backup Note.json")
									.setMimeType("text/plain")
									.setStarred(true).build();
							IntentSender intentSender = Drive.DriveApi
									.newCreateFileActivityBuilder()
									.setInitialMetadata(changeSet)
									.setInitialDriveContents(result.getDriveContents())
									.build(googleApiClient);
							try {
								((Activity) context).startIntentSenderForResult(
										intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
							} catch (IntentSender.SendIntentException e) {
								Log.w(TAG, "Unable to send intent", e);
							}
						}
					}.start();
				}
			};
}
