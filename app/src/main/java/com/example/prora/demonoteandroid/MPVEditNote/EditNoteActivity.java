package com.example.prora.demonoteandroid.MPVEditNote;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prora.demonoteandroid.R;
import com.example.prora.demonoteandroid.StateMaintainer;

public class EditNoteActivity extends AppCompatActivity implements MVP_EditNote.RequiredView {

	private static final String TAG = EditNoteActivity.class.getName();
	private MVP_EditNote.ProvidedPresenter providedPresenter;
	private EditText editTextNote;
	private StateMaintainer stateMaintainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		editTextNote = (EditText) findViewById(R.id.linedEditText);
		stateMaintainer = StateMaintainer.getInstance();
		setUpToolBar();
	}

	private void setUpToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null){
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

	private void setUpMVP(){
		if(stateMaintainer.firstTimeIn(R.layout.activity_edit_note)){
			PresenterEditNote presenterEditNote = new PresenterEditNote(this);
			ModelEditNote modelEditNote = new ModelEditNote(presenterEditNote);
			presenterEditNote.setModel(modelEditNote);
			providedPresenter = presenterEditNote;
		}else {
			providedPresenter = stateMaintainer.getStateProvidedPresenterEditNote(R.layout.activity_edit_note);
			providedPresenter.setView(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stateMaintainer.updateStateProvidedPresenterEditNote(R.layout.activity_edit_note, providedPresenter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id){
			case R.id.action_save:
				providedPresenter.saveNote(editTextNote.getText().toString());
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
	public void saveSuccess() {
		Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show();
	}

	@Override
	public Context getViewContext() {
		return this;
	}
}
