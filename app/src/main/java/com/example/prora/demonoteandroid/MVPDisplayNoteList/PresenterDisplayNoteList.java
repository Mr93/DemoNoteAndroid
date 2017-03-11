package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.example.prora.demonoteandroid.Note;
import com.example.prora.demonoteandroid.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.prora.demonoteandroid.Constant.KEY_SETTING_ROOT_FILE_DRIVE_ID;

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

	private void createTouchCallBack() {
		ItemTouchHelper.SimpleCallback simpleCallback = createCallBackSwipe();
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
		itemTouchHelper.attachToRecyclerView(getRecyclerView());
	}

	@NonNull
	private ItemTouchHelper.SimpleCallback createCallBackSwipe() {
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
				ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				model.deleteNoteFromDB(((NoteListAdapter.MyViewHolder)viewHolder).noteId);
				noteListContent.remove(viewHolder.getAdapterPosition());
				noteListAdapter.notifyDataSetChanged();
			}
		};
		return simpleCallback;
	}

	@Override
	public void setModel(MVP_DisplayNoteList.ProvidedModel model) {
		this.model = model;
	}

	@Override
	public void setView(MVP_DisplayNoteList.RequiredView view) {
		this.view = view;
	}

	@Override
	public void getNoteListFromDrive() {
		if(SettingsUtils.getInstances().getStringSharedPreferences(KEY_SETTING_ROOT_FILE_DRIVE_ID) == ""){
			Toast.makeText(getContext(), "Don't have any note in drive, you need to upload a note first", Toast.LENGTH_SHORT).show
					();
		}else {
			model.fetchNoteListFromDrive();
		}
	}

	@Override
	public Context getContext() {
		return view.getViewContext();
	}

	@Override
	public void updateDataFromDB(ArrayList<Note> noteListContent) {
		if(getRecyclerView() != null){
			this.noteListContent.clear();
			this.noteListContent.addAll(noteListContent);
			noteListAdapter.notifyDataSetChanged();
			Toast.makeText(getContext(), "Update data success", Toast.LENGTH_SHORT).show();
		}
	}

}
