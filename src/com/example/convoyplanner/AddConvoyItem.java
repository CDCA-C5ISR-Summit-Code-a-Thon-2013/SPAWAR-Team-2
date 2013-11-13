package com.example.convoyplanner;

import java.io.ByteArrayOutputStream;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddConvoyItem extends Activity {

	public static String TAG = "AddConvoyItem";
	public byte[] imageData = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_convoy_item);
	}

	public void btnTakeImageClicked(View btnClicked) {
		//
		dispatchTakePictureIntent(1);
		//
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == Activity.RESULT_CANCELED) {
			// No Photo
		} else if (resultCode == Activity.RESULT_OK) {
			//There was a photo taken
			//Change the image view in activity
			Bundle extras = data.getExtras();
			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			ImageView iv = (ImageView) findViewById(R.id.imgAddImage);
			iv.setImageBitmap(mImageBitmap);
			
			//Set the value of imageData
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageData = stream.toByteArray();
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Activity act = AddConvoyItem.this;
		act.startActivityForResult(takePictureIntent, actionCode);
	}

	public void btnSaveClicked(View btnClicked) {
		EditText nameField = (EditText) findViewById(R.id.inpTxtNomenclature);

		if (nameField.getText().toString().matches("")) {
			Toast.makeText(this,
					"Nomenclature is a required field.  Please add a name.",
					Toast.LENGTH_LONG).show();
		} else {
			// Add Item to database and change to main activity
			ConvoyItem newItem = new ConvoyItem();
			newItem.name = ((EditText) findViewById(R.id.inpTxtNomenclature))
					.getText().toString();
			newItem.nsn = ((EditText) findViewById(R.id.inpTxtNSN)).getText()
					.toString();
			newItem.notes = ((EditText) findViewById(R.id.inpTxtNotes))
					.getText().toString();
			newItem.vehicleID = ((EditText) findViewById(R.id.inpTxtVehicleID))
					.getText().toString();
					
			if(imageData!=null){
				newItem.bitmapData = imageData;
			}
			
			// Change to main Activity
			dbHelper.addConvoyItem(this, newItem);
			goToMainListView();
		}

	}

	public void goToMainListView() {
		Intent myIntent = new Intent(this, MainActivity.class);
		startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_convoy_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_GoToListView:
			goToMainListView();
			break;
		case R.id.action_ClearItemsFromAddActivity:
			dbHelper.clearAllItems(this);
			break;
		}
		//
		return super.onOptionsItemSelected(item);
	}

}
