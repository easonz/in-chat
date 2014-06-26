package pro.chinasoft.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InSQLiteOpenHelper extends SQLiteOpenHelper{

	
	
	private static final String TAG = InSQLiteOpenHelper.class.getSimpleName();
	private static final int version = 1; 
	
	private static final String DB_NAME = "mydata.db";
	private static final String DB_TABLE = "InMessage";

	private static final String DATABASE_CREATE = "create table " + DB_TABLE + "( "
			+ "Id integer primary key autoincrement, "
			+ "type integer not null, "
			+ "userId text, "
			+ "friendId text, "
			+ "content text, "
			+ "createDate number);";
	private static final String DATABASE_DROP = "DROP TABLE IF EXISTS " + DB_NAME;

	public InSQLiteOpenHelper(Context context){
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			Log.wtf(TAG, "onCreate database.");
			db.execSQL(DATABASE_CREATE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.wtf(TAG, "Upgrading database from version " + oldVersion
				+ "to " + newVersion + ", which will destroy all old data.");
		db.execSQL(DATABASE_DROP);
		onCreate(db);
	}

}
