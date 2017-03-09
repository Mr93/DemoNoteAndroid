package com.example.prora.demonoteandroid.GoogleDriveApi;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by prora on 3/9/2017.
 */

public class GoogleDriveHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = GoogleDriveHelper.class.getSimpleName();
	private GoogleApiClient googleApiClient;
	public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;
	private Context context;

	private static GoogleDriveHelper instance;

	private GoogleDriveHelper (){

	}

	public static GoogleDriveHelper getInstance(){
		if(instance == null){
			instance = new GoogleDriveHelper();
		}
		return instance;
	}

	public void createApiClient(Context context){
		if(googleApiClient == null){
			googleApiClient = new GoogleApiClient.Builder(context)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
	}

	public void connectGoogleDrive(Context context){
		this.context = context;
		if(googleApiClient != null){
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d(TAG, "onConnected: ");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended: ");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
		Activity activity = (Activity) context;
		if(activity != null && activity.hasWindowFocus()){
			if(connectionResult.hasResolution()){
				try {
					Log.d(TAG, "onConnectionFailed: 1 " + connectionResult.getErrorCode());
					connectionResult.startResolutionForResult((Activity) context, RESOLVE_CONNECTION_REQUEST_CODE);
				} catch (IntentSender.SendIntentException e) {
					Log.e(TAG, "onConnectionFailed: ", e);
					e.printStackTrace();
				}
			}else {
				Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorCode());
				GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), ((Activity)context), 0).show();
			}
		}

	}
}
