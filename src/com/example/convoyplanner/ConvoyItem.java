package com.example.convoyplanner;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ConvoyItem {

	public Integer id = -1;
	public String name = "";
	public String nsn = "";
	public String notes = "";
	public String vehicleID = "";
	public byte[] bitmapData = null;
	
	public ConvoyItem(){
		
	}
	
	public ConvoyItem(Cursor cur){
		
		id = cur.getInt(cur.getColumnIndex(Table.rowID));
		name = cur.getString(cur.getColumnIndex(Table.rowNomencalture));
		nsn = cur.getString(cur.getColumnIndex(Table.rowNSN));
		notes = cur.getString(cur.getColumnIndex(Table.rowNotes));
		vehicleID = cur.getString(cur.getColumnIndex(Table.rowVehicleID));
		//
		byte[] b = cur.getBlob(cur.getColumnIndex(Table.rowBitmapData));
		if(b!=null &&  b.length>0){
			bitmapData  = b;
					//BitmapFactory.decodeByteArray(b, 0, b.length);
		}
	}
	
	public static class Table{
				
		public static String tableName = "Convoy_Items";
		
		public static String rowID = "Item_ID";
		public static String rowNomencalture = "Item_Nomenclature";
		public static String rowNSN = "Item_NSN";
		public static String rowBitmapData = "Item_Image";
		public static String rowNotes = "Item_Notes";
		public static String rowVehicleID = "Item_Vehicle";
		
	}
	
}
