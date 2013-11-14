package com.example.convoyplanner;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Convoy Items";

	public dbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String argSTR = "CREATE TABLE " + ConvoyItem.Table.tableName + " ("
				+ ConvoyItem.Table.rowID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ ConvoyItem.Table.rowNomencalture + " TEXT, "
				+ ConvoyItem.Table.rowNSN + " TEXT, "
				+ ConvoyItem.Table.rowBitmapData + " BLOB, "
				+ ConvoyItem.Table.rowNotes + " TEXT, "
				+ ConvoyItem.Table.rowVehicleID + " TEXT);";
		
		db.execSQL(argSTR);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	
	public static String itemsToHTMLTable(Context c){
		dbHelper db = new dbHelper(c);
		Cursor cur = db.getReadableDatabase().rawQuery("SELECT * FROM "+ ConvoyItem.Table.tableName, null);
		ArrayList<ConvoyItem> all = new ArrayList<ConvoyItem>();
		if(cur.moveToFirst()){
			do{
				all.add(new ConvoyItem(cur));
			}while(cur.moveToNext());
		}
		//
		String rowStr = "";
		rowStr+=String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", "NAME","NSN","VEHICLE","NOTES");
		for(ConvoyItem ci : all){
			rowStr+=String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", ci.name,ci.nsn,ci.vehicleID,ci.notes);
		}
		//
		db.close();
		return String.format("<html><head></head><body><table>%s</table></body></html>", rowStr);
	}
	
	
	public static void clearAllItems(Context c){
		dbHelper db = new dbHelper(c);
		db.getWritableDatabase().delete(ConvoyItem.Table.tableName, null, null);
		db.close();
	}
	
	public static void addConvoyItem(Context c, ConvoyItem cItem){
		dbHelper db = new dbHelper(c);
		
		ContentValues cv = new ContentValues();
		cv.put(ConvoyItem.Table.rowNomencalture, cItem.name);
		cv.put(ConvoyItem.Table.rowNotes, cItem.notes);
		cv.put(ConvoyItem.Table.rowNSN, cItem.nsn);
		cv.put(ConvoyItem.Table.rowVehicleID, cItem.vehicleID);
		cv.put(ConvoyItem.Table.rowBitmapData, cItem.bitmapData);
		//
		db.getWritableDatabase().insert(ConvoyItem.Table.tableName, null, cv);
		//
		db.close();
	}
	
	public static void removeConvoyItem(Context c, ConvoyItem item){
		dbHelper db = new dbHelper(c);
		db.getWritableDatabase().delete(ConvoyItem.Table.tableName, ConvoyItem.Table.rowID + "==" + item.id, null);
		db.close();
	}
	
	public static ArrayList<ConvoyItem> getAllConvoyItems(Context c){
		ArrayList<ConvoyItem> rVal = new ArrayList<ConvoyItem>();
		
		dbHelper db = new dbHelper(c);
				
		Cursor cur = db.getReadableDatabase().rawQuery("SELECT * FROM " + ConvoyItem.Table.tableName, null);
		if(cur.moveToFirst()){
			do{
				ConvoyItem newItem = new ConvoyItem(cur);
				rVal.add(newItem);
			}while(cur.moveToNext());
		}
		
		db.close();
		return rVal;
	}

}
