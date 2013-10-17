package com.fai.minhasfinancas;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
	private double saldo = 0.0;
	private TextView total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Para recuperar alguma sharedPreference, se preciso
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		total = (TextView) findViewById(R.id.tvSaldo);
		
		dialog = ProgressDialog.show(this,
				getResources().getString(R.string.loading), getResources()
						.getString(R.string.loading1), true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				saldo = 0.0;
				fillValues();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(saldo > 0){
							total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
							total.setTextColor(Color.BLUE);
						}else {
							total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
							total.setTextColor(Color.RED);
						}
						
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
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final int menuItemPosition = info.position;
		int menuItemIndex = item.getItemId();
		final Entry entry = entries.get(menuItemPosition);
		
		switch (menuItemIndex) {
		case 0:
			final Dialog edit = new Dialog(this);
			edit.setContentView(R.layout.activity_new_entry);
			edit.setTitle("Editar valor");
			
			final Button btnCancel = (Button) edit.findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					edit.dismiss();
				}
			});
			
			final EditText value = (EditText) edit.findViewById(R.id.etValue);
			
			final Button btnOK = (Button) edit.findViewById(R.id.btnSave);
			btnOK.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					db = new EntryOpenHelper(getApplicationContext(), null, null, 1);
					db.getWritableDatabase();

					try {
						Float etValue = Float.valueOf(value.getText().toString());
						
						if(entry.getType() == 0){
							saldo -= entry.getValue();
							saldo += etValue;
						} else {
							saldo += entry.getValue();
							saldo -= etValue;
						}
						
						entry.setValue(etValue);
						entries.set(menuItemPosition, entry);
						db.updateEntry(entry);
						db.close();
						
						
						new Thread(new Runnable() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if(saldo > 0){
											total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
											total.setTextColor(Color.BLUE);
										} else {
											total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
											total.setTextColor(Color.RED);
										}
										
										listView = (ListView) findViewById(R.id.listEntries);
				
										EntryAdapter adapter = new EntryAdapter(
												getApplicationContext(), entries);
				
										listView.setAdapter(adapter);
				
										listView.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> arg0,
													View v, int position, long arg3) {

//												Toast.makeText(getApplicationContext(), "Item clicado: " + position, Toast.LENGTH_LONG).show();
											}
										});
										dialog.dismiss();
									}
								});				
							}
						}).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					edit.dismiss();
				}
			});
			
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(edit.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			edit.show();
			edit.getWindow().setAttributes(lp);
			break;
		case 1:
			AlertDialog alerta;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.confirmation);
			builder.setMessage(R.string.delete_confirmation);
			builder.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							db.getWritableDatabase();
							db.removeEntry(String.valueOf(entry.getId()));
							db.close();
													
							if(entry.getType() == 0){
								saldo -= entry.getValue();
							} else {
								saldo += entry.getValue();
							}
							
							entries.remove(menuItemPosition);			
							
							new Thread(new Runnable() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if(saldo > 0){
												total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
												total.setTextColor(Color.BLUE);
											} else {
												total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
												total.setTextColor(Color.RED);
											}
											
											listView = (ListView) findViewById(R.id.listEntries);
					
											EntryAdapter adapter = new EntryAdapter(
													getApplicationContext(), entries);
					
											listView.setAdapter(adapter);
					
											listView.setOnItemClickListener(new OnItemClickListener() {

												@Override
												public void onItemClick(AdapterView<?> arg0,
														View v, int position, long arg3) {

//													Toast.makeText(getApplicationContext(), "Item clicado: " + position, Toast.LENGTH_LONG).show();
												}
											});
											dialog.dismiss();
										}
									});				
								}
							}).start();
						}
					});
			builder.setNegativeButton(R.string.no, null);
			alerta = builder.create();
			alerta.show();
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
			
		case R.id.action_credit:
			Intent iCredit = new Intent(this, NewEntryActivity.class);
			iCredit.putExtra("type", "credit");
			startActivityForResult(iCredit, 1);
			break;
			
		case R.id.action_debit:
			Intent iDebit = new Intent(this, NewEntryActivity.class);
			iDebit.putExtra("type", "debit");
			startActivityForResult(iDebit, 1);
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
			
			for (int i = 0; i < entries.size(); i++) {
				if(entries.get(i).getType() == 0){
					saldo += entries.get(i).getValue();
				}else{
					saldo -= entries.get(i).getValue();
				}
			}
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
					saldo = 0.0;
					fillValues();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(saldo > 0){
								total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
								total.setTextColor(Color.BLUE);
							} else {
								total.setText("Saldo: R$ " + new DecimalFormat("0.00").format(saldo));
								total.setTextColor(Color.RED);
							}
							
							listView = (ListView) findViewById(R.id.listEntries);
	
							EntryAdapter adapter = new EntryAdapter(
									getApplicationContext(), entries);
	
							listView.setAdapter(adapter);
	
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View v, int position, long arg3) {

//									Toast.makeText(getApplicationContext(), "Item clicado: " + position, Toast.LENGTH_LONG).show();
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
