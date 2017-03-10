package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;
import com.example.prora.demonoteandroid.R;
import com.example.prora.demonoteandroid.SettingsUtils;
import com.example.prora.demonoteandroid.StateMaintainer;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import static com.example.prora.demonoteandroid.Constant.KEY_SETTING_ROOT_FILE_DRIVE_ID;
import static com.example.prora.demonoteandroid.Constant.REQUEST_CODE_CREATOR;
import static com.example.prora.demonoteandroid.Constant.RESOLVE_CONNECTION_REQUEST_CODE;

public class EditNoteActivity extends AppCompatActivity implements MVP_EditNote.RequiredView {

	private static final String TAG = EditNoteActivity.class.getName();
	private MVP_EditNote.ProvidedPresenter providedPresenter;
	private EditText editTextNote;
	private StateMaintainer stateMaintainer;
	private int noteId = ModelEditNote.ID_ERROR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		editTextNote = (EditText) findViewById(R.id.linedEditText);
		stateMaintainer = StateMaintainer.getInstance();
		setUpToolBar();
		if (getIntent().getExtras() != null) {
			noteId = getIntent().getExtras().getInt("noteId");
			editTextNote.setText(getIntent().getExtras().getString("content"));
			editTextNote.setSelection(editTextNote.getText().length());
		}
	}

	private void setUpToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		setUpMVP();
	}

	private void setUpMVP() {
		if (stateMaintainer.firstTimeIn(R.layout.activity_edit_note)) {
			PresenterEditNote presenterEditNote = new PresenterEditNote(this);
			ModelEditNote modelEditNote = new ModelEditNote(presenterEditNote);
			presenterEditNote.setModel(modelEditNote);
			providedPresenter = presenterEditNote;
		} else {
			providedPresenter = (MVP_EditNote.ProvidedPresenter) stateMaintainer.getPresenter(R.layout.activity_edit_note);
			providedPresenter.setView(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stateMaintainer.updatePresenterState(R.layout.activity_edit_note, providedPresenter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Note note = new Note(editTextNote.getText().toString(), noteId);
		switch (id) {
			case R.id.action_save:
				providedPresenter.saveNote(note);
				break;
			case R.id.action_upload:
				if(note.noteId == -1){
					Toast.makeText(this, "You need save first", Toast.LENGTH_SHORT).show();
				}else {
					providedPresenter.upload(note);
				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void saveError(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void saveSuccess(Note note) {
		this.noteId = note.noteId;
		Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show();
	}

	@Override
	public Context getViewContext() {
		return this;
	}

	@Override
	public int getNoteId() {
		return noteId;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Note note = new Note(editTextNote.getText().toString(), noteId);
		switch (requestCode) {
			case RESOLVE_CONNECTION_REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					providedPresenter.upload(note);
				}
				break;
			case REQUEST_CODE_CREATOR:
				if (resultCode == RESULT_OK) {
					DriveId driveId = (DriveId) data.getParcelableExtra(
							OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
					SettingsUtils.getInstances().setStringSharedPreferences(KEY_SETTING_ROOT_FILE_DRIVE_ID, driveId.toString());
				}
				break;
			default:
				break;
		}
	}
}
