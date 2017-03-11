package com.example.prora.demonoteandroid;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.prora.demonoteandroid.MPVEditNote.ModelEditNote;
import com.example.prora.demonoteandroid.MVPDisplayNoteList.Note;

import java.util.ArrayList;

/**
 * Created by prora on 3/8/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = DBHelper.class.getSimpleName();
	private static DBHelper instance;
	private static final String DATABASE_NAME = "MyNoteDatabase.db";
	private static final int DATABASE_VERSION = 1;
	private static final String NOTE_TABLE_NAME = "note";
	private static final String NOTE_COLUMN_ID = "id";
	private static final String NOTE_COLUMN_CONTENT = "content";

	private DBHelper (Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized static DBHelper getInstance(Context context){
		if(instance == null){
			instance = new DBHelper(context.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"create table " + NOTE_TABLE_NAME +
						" (" + NOTE_COLUMN_ID + " integer primary key, " + NOTE_COLUMN_CONTENT +" text)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
		onCreate(db);
	}


	public synchronized int insertNote (String content){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(NOTE_COLUMN_CONTENT, content);
		return (int) sqLiteDatabase.insert(NOTE_TABLE_NAME, null, contentValues);
	}

	public synchronized int insertNoteWithId (String content, int id){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(NOTE_COLUMN_ID, id);
		contentValues.put(NOTE_COLUMN_CONTENT, content);
		return (int) sqLiteDatabase.insert(NOTE_TABLE_NAME, null, contentValues);
	}

	public synchronized int updateNote(String content, int id){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(NOTE_COLUMN_CONTENT, content);
		sqLiteDatabase.update(NOTE_TABLE_NAME, contentValues, " id = ? ", new String[]{Integer.toString(id)});
		return id;
	}

	public synchronized Integer deleteNote(Integer id){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Log.d(TAG, "deleteNote: " + id);
		return sqLiteDatabase.delete(NOTE_TABLE_NAME, "id = ? ", new String[] {Integer.toString(id)});
	}

	public ArrayList<Note> getAllNotes(){
		ArrayList<Note> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + NOTE_TABLE_NAME, null);
		data.moveToFirst();
		while (data.isAfterLast() == false){
			Note note = new Note(data.getString(data.getColumnIndex(NOTE_COLUMN_CONTENT)),
					data.getInt(data.getColumnIndex(NOTE_COLUMN_ID)));
			arrayList.add(note);
			data.moveToNext();
		}
		return arrayList;
	}

	public String getANote(int id){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + NOTE_TABLE_NAME + " where " + NOTE_COLUMN_ID + " = " + id +"",
				null);
		Log.d(TAG, "getANote: " + "select * from " + NOTE_TABLE_NAME + " where " + NOTE_COLUMN_ID + " = " + id +"");
		data.moveToFirst();
		return data.getString(data.getColumnIndex(NOTE_COLUMN_CONTENT));
	}
}
