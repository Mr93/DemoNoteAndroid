package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.prora.demonoteandroid.MPVEditNote.EditNoteActivity;
import com.example.prora.demonoteandroid.MPVEditNote.ModelEditNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prora on 3/9/2017.
 */

public class PresenterDisplayNoteList implements MVP_DisplayNoteList.ProvidedPresenter, MVP_DisplayNoteList.RequiredPresenter {

	private static final String TAG = PresenterDisplayNoteList.class.getSimpleName();
	private List<Note> noteListContent;
	private NoteListAdapter noteListAdapter;
	private MVP_DisplayNoteList.RequiredView view;
	private MVP_DisplayNoteList.ProvidedModel model;

	public PresenterDisplayNoteList(MVP_DisplayNoteList.RequiredView view) {
		this.view = view;
		noteListContent = new ArrayList<>();
		noteListAdapter = new NoteListAdapter(noteListContent);
	}

	private RecyclerView getRecyclerView() {
		return view.getRecyclerView();
	}


	@Override
	public void initRecyclerView() {
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		getRecyclerView().setLayoutManager(layoutManager);
		getRecyclerView().setItemAnimator(new DefaultItemAnimator());
		getRecyclerView().setAdapter(noteListAdapter);
		createTouchCallBack();
		model.loadNoteListFromDB();
	}

	@Override
	public void setModel(MVP_DisplayNoteList.ProvidedModel model) {
		this.model = model;
	}

	@Override
	public void setView(MVP_DisplayNoteList.RequiredView view) {
		this.view = view;
	}

	private void createTouchCallBack() {
		ItemTouchHelper.SimpleCallback simpleCallback = createCallBackSwipe();
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
		itemTouchHelper.attachToRecyclerView(getRecyclerView());
	}

	@NonNull
	private ItemTouchHelper.SimpleCallback createCallBackSwipe() {
		Log.d(TAG, "createCallBackSwipe: " + System.identityHashCode(this));
		ItemTouchHelper.SimpleCallback temp = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
				ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				Log.d(TAG, "createCallBackSwipe: " + System.identityHashCode(PresenterDisplayNoteList.this));
				noteListContent.remove(viewHolder.getAdapterPosition());
				noteListAdapter.notifyDataSetChanged();
			}
		};
		return temp;
	}

	@Override
	public Context getContext() {
		return view.getViewContext();
	}

	@Override
	public void updateDataFromDB(ArrayList<Note> noteListContent) {
		if(getRecyclerView() != null){
			for(int i = 0; i < noteListContent.size(); i++){
				Log.d(TAG, "updateDataFromDB: " + noteListContent.get(i).noteId);
				Log.d(TAG, "updateDataFromDB: " + noteListContent.get(i).noteContent);
			}
			this.noteListContent.clear();
			this.noteListContent.addAll(noteListContent);
			noteListAdapter.notifyDataSetChanged();
		}
	}
}
