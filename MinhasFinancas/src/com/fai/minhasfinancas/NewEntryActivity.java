package com.fai.minhasfinancas;

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fai.minhasfinancas.entity.Entry;
import com.fai.minhasfinancas.sqlite.EntryOpenHelper;

public class NewEntryActivity extends Activity {
	
	private EntryOpenHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_entry);
		
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				db = new EntryOpenHelper(getApplicationContext(), null, null, 1);
				db.getWritableDatabase();
		
				try {
					EditText etValue = (EditText) findViewById(R.id.etValue);
					
					Entry entry = new Entry();
					entry.setName("Item novo");
					entry.setValue(Float.parseFloat(etValue.getText().toString()));
					entry.setType(0);
					
					db.addEntry(entry);
					db.close();
					
					//Devolve o resultado para a MainActivity indicando que eh para atualizar
					Intent resultIntent = new Intent();
					resultIntent.putExtra("update", "update");
					setResult(Activity.RESULT_OK, resultIntent);
					
					finish();
				} catch (Exception e) {
					e.printStackTrace();
					finish();
				}
			}
		});
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
