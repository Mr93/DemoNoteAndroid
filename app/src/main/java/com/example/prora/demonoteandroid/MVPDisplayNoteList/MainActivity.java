package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.prora.demonoteandroid.GoogleDriveApi.DriveClientConnectedCallBack;
import com.example.prora.demonoteandroid.GoogleDriveApi.GoogleDriveHelper;
import com.example.prora.demonoteandroid.MPVEditNote.EditNoteActivity;
import com.example.prora.demonoteandroid.R;
import com.example.prora.demonoteandroid.StateMaintainer;

public class MainActivity extends AppCompatActivity implements MVP_DisplayNoteList.RequiredView {

	private static final String TAG = MainActivity.class.getSimpleName();
	private RecyclerView recyclerView;
	private StateMaintainer stateMaintainer;

	private MVP_DisplayNoteList.ProvidedPresenter providedPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stateMaintainer = StateMaintainer.getInstance();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
				startActivity(intent);
			}
		});
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		GoogleDriveHelper.getInstance(this).createApiClient();
		GoogleDriveHelper.getInstance(this).connectGoogleDrive();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setUpMVP();
		providedPresenter.initRecyclerView();
	}

	private void setUpMVP(){
		if(stateMaintainer.firstTimeIn(R.layout.activity_main)){
			PresenterDisplayNoteList presenterEditNote = new PresenterDisplayNoteList(this);
			ModelDisplayNoteList modelEditNote = new ModelDisplayNoteList(presenterEditNote);
			presenterEditNote.setModel(modelEditNote);
			providedPresenter = presenterEditNote;
		}else {
			providedPresenter = (MVP_DisplayNoteList.ProvidedPresenter) stateMaintainer.getPresenter(R.layout.activity_main);
			providedPresenter.setView(this);
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		stateMaintainer.updatePresenterState(R.layout.activity_main, providedPresenter);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public RecyclerView getRecyclerView() {
		return recyclerView;
	}

	@Override
	public Context getViewContext() {
		return this;
	}

}
