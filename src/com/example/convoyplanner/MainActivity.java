package com.example.convoyplanner;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public ArrayList<ConvoyItem> allItems = new ArrayList<ConvoyItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		allItems = dbHelper.getAllConvoyItems(this);
		if (allItems.size() == 0) {
			goToAddItemActivity();
		} else {
			mainListAdapter mla = new mainListAdapter(this,
					R.layout.supply_item, allItems);

			ListView lv = (ListView) findViewById(R.id.listMainListView);
			lv.setAdapter(mla);
			registerForContextMenu(lv);
		}

	}

	private void goToAddItemActivity() {
		Intent myIntent = new Intent(this, AddConvoyItem.class);
		startActivity(myIntent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listview_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.action_delete_lstItem:
			// delete the item
			try {
				ListView lv = (ListView) findViewById(R.id.listMainListView);
				mainListAdapter mla = (mainListAdapter) lv.getAdapter();
				mla.remove(allItems.get(info.position));
				allItems = dbHelper.getAllConvoyItems(this);
				// mla.notifyDataSetChanged();
				Toast.makeText(this, "Item Removed From Convoy Inventory",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {

			}
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//
		switch (item.getItemId()) {
		case R.id.action_addItem:
			goToAddItemActivity();
			break;
		case R.id.action_ClearItems:
			//
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						dbHelper.clearAllItems(MainActivity.this);
						goToAddItemActivity();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
		
			break;

		case R.id.action_emailItems:

			String htmlStr = dbHelper.itemsToHTMLTable(this);

			Toast.makeText(
					this,
					"All convoy inventory has been sent to your preferred email application.",
					Toast.LENGTH_LONG).show();

			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/html");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Convoy Inventory");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					Html.fromHtml(htmlStr));
			startActivity(Intent.createChooser(emailIntent, "Email:"));
			//
			break;

		}

		//
		return super.onOptionsItemSelected(item);
	}

	public static class mainListAdapter extends ArrayAdapter<ConvoyItem> {

		public static String TAG = "ListItemAdapter";
		private LayoutInflater inflater = null;
		private ArrayList<ConvoyItem> list = null;

		public mainListAdapter(Context context, int resource,
				ArrayList<ConvoyItem> objects) {
			super(context, resource, objects);
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			list = objects;
		}

		@Override
		public void remove(ConvoyItem object) {

			dbHelper.removeConvoyItem(this.getContext(), object);

			super.remove(object);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View newView = convertView;
			//
			if (convertView == null) {
				newView = inflater.inflate(R.layout.supply_item, null);

			}

			ConvoyItem ci = list.get(position);

			((TextView) newView.findViewById(R.id.lstTxtNomenclature))
					.setText(ci.name);
			((TextView) newView.findViewById(R.id.lstTxtPartNSN))
					.setText(ci.nsn);
			((TextView) newView.findViewById(R.id.lstTxtVehicle))
					.setText(ci.vehicleID.matches("") ? "" : "Vehicle:"
							+ ci.vehicleID);

			if (ci.bitmapData != null && ci.bitmapData.length > 100) {
				// Log.d(TAG, ci.bitmapData.length + "");
				ImageView iv = (ImageView) newView
						.findViewById(R.id.lstImgItem);
				iv.setImageBitmap(BitmapFactory.decodeByteArray(ci.bitmapData,
						0, ci.bitmapData.length));
			}

			return newView;

		}

	}
}