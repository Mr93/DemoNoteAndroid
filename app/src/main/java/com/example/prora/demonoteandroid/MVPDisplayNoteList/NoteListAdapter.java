package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prora.demonoteandroid.MPVEditNote.EditNoteActivity;
import com.example.prora.demonoteandroid.R;

import java.util.List;

/**
 * Created by prora on 3/9/2017.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder>{

	private static final String TAG = NoteListAdapter.class.getSimpleName();
	private List<Note> noteListContent;

	public NoteListAdapter(List<Note> noteListContent) {
		this.noteListContent = noteListContent;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_row, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		String noteContent = noteListContent.get(position).noteContent;
		holder.noteContent.setText(noteContent);
		holder.noteId = noteListContent.get(position).noteId;
	}

	@Override
	public int getItemCount() {
		return noteListContent.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView noteContent;
		public int noteId;

		public MyViewHolder(View view) {
			super(view);
			this.noteContent = (TextView) view.findViewById(R.id.noteContent);
			this.noteContent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
					intent.putExtra("content", noteContent.getText().toString());
					intent.putExtra("noteId", noteId);
					v.getContext().startActivity(intent);
				}
			});
		}
	}
}
