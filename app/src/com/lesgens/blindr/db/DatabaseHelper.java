package com.lesgens.blindr.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.models.Message;
import com.lesgens.blindr.models.User;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static DatabaseHelper instance;
	
	private DatabaseHelper(Context context) {
		super(context, "db", null, 1);
	}
	
	public static void init(final Context context){
		if(instance != null){
			return;
		}
		instance = new DatabaseHelper(context);
	}
	
	public static DatabaseHelper getInstance(){
		return instance;
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE blindr_message (id INTEGER PRIMARY KEY AUTOINCREMENT, message_id TEXT, conversation TEXT, fakeName TEXT, realName TEXT, message TEXT, timestamp LONG, isIncoming INTEGER DEFAULT 0);");
		db.execSQL("CREATE TABLE blindr_last_message (id INTEGER PRIMARY KEY AUTOINCREMENT, remoteId TEXT, timestamp LONG);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE if exists blindr_message");
		db.execSQL("DROP TABLE if exists blindr_last_message");
		onCreate(db);
	}

	public void addMessage(Message m, String remoteId){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("message_id",m.getId().toString());
		cv.put("fakeName", m.getFakeName());
		cv.put("realName", m.getRealName());
		cv.put("message", m.getMessage());
		cv.put("isIncoming", m.isIncoming() ? 1 : 0);
		cv.put("timestamp", m.getTimestamp().getTime());
		cv.put("conversation", remoteId);
		db.insert("blindr_message", null, cv);
		
		cv = new ContentValues();
		cv.put("timestamp", m.getTimestamp().getTime());
		if(getLastMessageFetched(remoteId) == 0){
			cv.put("remoteId", remoteId);
			db.insert("blindr_last_message", null, cv);
		} else{
			db.update("blindr_last_message", cv, "remoteId = ?", new String[]{m.getUser().getId()});
		}
		
	}

	public ArrayList<Message> getPrivateMessages(User user){
		ArrayList<Message> messages = new ArrayList<Message>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT message_id, timestamp, realName, fakeName, message, isIncoming FROM blindr_message WHERE conversation = ? ORDER BY timestamp ASC;", new String[]{user.getId()} );
		
		Message message;
		while(c.moveToNext()){
			UUID id = UUID.fromString(c.getString(0));
			Timestamp timestamp = new Timestamp(c.getLong(1));
			String realName = c.getString(2);
			String fakeName = c.getString(3);
			String text = c.getString(4);
			boolean isIncoming = c.getInt(5) == 1;
			message = new Message(id, timestamp, isIncoming ? user : Controller.getInstance().getMyself(), 
					realName, fakeName, text, isIncoming);
			messages.add(message);
		}
		
		return messages;
	}
	
	public long getLastMessageFetched(String remoteId){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT timestamp FROM blindr_last_message WHERE remoteId = ?;", new String[]{remoteId} );
		
		if(c.moveToFirst()){
			return c.getLong(0);
		}
		
		return 0;		
	}

	public void eraseBD(){
		this.onUpgrade(getWritableDatabase(), 0, 1);
	}

}