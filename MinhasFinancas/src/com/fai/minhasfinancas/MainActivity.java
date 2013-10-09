package com.fai.minhasfinancas;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fai.minhasfinancas.adapter.EntryAdapter;
import com.fai.minhasfinancas.entity.Entry;
import com.fai.minhasfinancas.sqlite.EntryOpenHelper;

public class MainActivity extends SherlockActivity {
	
	private ProgressDialog dialog;
	private ListView listView;
	private SharedPreferences prefs;
	private List<Entry> entries;
	private EntryOpenHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Para recuperar alguma sharedPreference, se preciso
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		dialog = ProgressDialog.show(this,
				getResources().getString(R.string.loading), getResources()
						.getString(R.string.loading1), true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				fillValues();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listView = (ListView) findViewById(R.id.listEntries);

						EntryAdapter adapter = new EntryAdapter(
								getApplicationContext(), entries);

						listView.setAdapter(adapter);

						listView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View v, int position, long arg3) {

								Toast.makeText(getApplicationContext(), "Item clicado: " + position, Toast.LENGTH_LONG).show();
							}
						});
						registerForContextMenu(listView);
						dialog.dismiss();
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.listEntries) {
			menu.setHeaderTitle(R.string.options);
			menu.add(Menu.NONE, 0, 0, R.string.menu_context_1);
			menu.add(Menu.NONE, 1, 1, R.string.menu_context_2);
			menu.add(Menu.NONE, 2, 2, R.string.menu_context_3);
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemPosition = info.position;
		int menuItemIndex = item.getItemId();
		final Entry entry = entries.get(menuItemPosition);
		
		switch (menuItemIndex) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent iSettings = new Intent(this, SettingsActivity.class);
			startActivityForResult(iSettings, 0);
			break;
			
		case R.id.action_new:
			Intent iNew = new Intent(this, NewEntryActivity.class);
			startActivityForResult(iNew, 1);
			break;

		default:
			break;
		}
		return true;
	}
	
	private void fillValues() {		
		if (entries != null) {
			entries.clear();
		} else {
			entries = new ArrayList<Entry>();
		}

		//usando dados dummies
//		Entry entry = new Entry();
//		entry.setId(0);
//		entry.setName("Primeira entrada");
//		entry.setType(0);
//		
//		Entry entry2 = new Entry();
//		entry2.setId(1);
//		entry2.setName("Segunda entrada");
//		entry2.setType(1);
//		
//		entries.add(entry);
//		entries.add(entry2);
		
		//usando banco de dados
		db = new EntryOpenHelper(getApplicationContext(), null, null, 1);
		db.getWritableDatabase();

		try {
			entries = db.getAll();
			db.close();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK
				&& data.getStringExtra("update") != null){
			// Recarrego os dados
			dialog = ProgressDialog.show(this,
								getResources().getString(R.string.loading), getResources()
										.getString(R.string.loading1), true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					fillValues();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							listView = (ListView) findViewById(R.id.listEntries);
	
							EntryAdapter adapter = new EntryAdapter(
									getApplicationContext(), entries);
	
							listView.setAdapter(adapter);
	
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View v, int position, long arg3) {

									Toast.makeText(getApplicationContext(), "Item clicado: " + position, Toast.LENGTH_LONG).show();
								}
							});
							dialog.dismiss();
						}
					});				
				}
			}).start();
		}
	}

}
