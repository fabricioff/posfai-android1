package com.fai.minhasfinancas.sqlite;

import java.text.ParseException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;

import com.fai.minhasfinancas.entity.Entry;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EntryOpenHelper extends SQLiteOpenHelper {
	
	private final String NO_REGISTER = "NO REGISTER";

	private static String DATABASENAME = "minhasfinancas.db";
	private ArrayList<Entry> entries = new ArrayList<Entry>();

	public EntryOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASENAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE entry(id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date" + " DATETIME not null," + "value" + " REAL not null," + "type" + " INTEGER not null,"
				+ "description" + " TEXT not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Ao atualizar a versao
		Log.w("EntryOpenHelper", "onUpgrade - OldVersion: " + oldVersion);
		Log.w("EntryOpenHelper", "onUpgrade - NewVersion: " + newVersion);
		
	}

	public void addEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		String sql = "INSERT INTO entry (date, value, type, description) VALUES(?,?,?,?)";

		SQLiteStatement insertStmt = db.compileStatement(sql);
		insertStmt.clearBindings();

		if (entry.getDate() != null) {
			insertStmt.bindString(1, entry.getDate());
		} else {
			insertStmt.bindString(1, "");
		}
		
		insertStmt.bindDouble(2, entry.getValue());

		insertStmt.bindLong(3, entry.getType());
		
		if (entry.getDescription() != null) {
			insertStmt.bindString(4, entry.getDescription());
		} else {
			insertStmt.bindString(4, NO_REGISTER);
		}
		
		insertStmt.executeInsert();
		db.close();
	}

	public void updateEntry(Entry entry) {
		SQLiteDatabase db = getWritableDatabase();

		String sql = "UPDATE entry set date = ?, value = ?, type = ?, description = ? where id = ?";

		SQLiteStatement insertStmt = db.compileStatement(sql);
		insertStmt.clearBindings();
		
		if (entry.getDate() != null) {
			insertStmt.bindString(1, entry.getDate());
		} else {
			insertStmt.bindString(1, "");
		}

		insertStmt.bindDouble(2, entry.getValue());
		
		insertStmt.bindLong(3, entry.getType());
		
		if (entry.getDescription() != null) {
			insertStmt.bindString(4, entry.getDescription());
		} else {
			insertStmt.bindString(4, NO_REGISTER);
		}
		
		insertStmt.bindLong(5, entry.getId());
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			insertStmt.execute();
		} else {
			insertStmt.executeUpdateDelete();
		}

		db.close();
	}

	public void emptyEntries() {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("delete from entry");
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeEntry(String id) {
		try {
			String[] args = { id };
			getWritableDatabase().delete("entry", "id=?", args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Entry> getAll() throws ParseException {

		entries.clear();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from entry order by date", null);
		if (cursor.getCount() != 0) {
			if (cursor.moveToFirst()) {
				do {
					Entry entry = new Entry();

					entry.setId(cursor.getInt(cursor.getColumnIndex("id")));
									
					entry.setDate(cursor.getString(cursor
							.getColumnIndex("date")));

					entry.setValue(cursor.getFloat(cursor
							.getColumnIndex("value")));
					
					entry.setType(cursor.getInt(cursor
							.getColumnIndex("type")));
					
					entry.setDescription(cursor.getString(cursor
							.getColumnIndex("description")));					
					
					entries.add(entry);

				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return entries;
	}

	public Entry getEntryById(int id) throws ParseException {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from entry where id = ?",
				new String[] { String.valueOf(id) });

		Entry entry = null;

		if (cursor.getCount() != 0) {
			if (cursor.moveToFirst()) {
				entry = new Entry();

				entry.setId(cursor.getInt(cursor.getColumnIndex("id")));
				entry.setDate(cursor.getString(cursor.getColumnIndex("date")));
				entry.setValue(cursor.getFloat(cursor.getColumnIndex("value")));
				entry.setType(cursor.getInt(cursor.getColumnIndex("type")));
				entry.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			}
		}
		cursor.close();
		db.close();
		return entry;
	}
}
