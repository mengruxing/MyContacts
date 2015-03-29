package guet.mrx.mycontacts;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DAO {
	private DBOpenHelper helper;

	public DAO(Context context) {
		helper = new DBOpenHelper(context);
	}

	/**
	 * 获取当前已存在表的列表
	 */
	public List<String> getCurrentTables() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT name FROM current_tables", null);
		List<String> currentTables = new ArrayList<String>();
		while (c.moveToNext()) {
			currentTables.add(c.getString(0));
		}
		switch (currentTables.size()) {
		case 0:
			currentTables.add("");
			break;
		case 1:
			break;
		default:
			currentTables.add(0, "所有联系人");
			break;
		}
		c.close();
		db.close();
		return currentTables;
	}

	// 新建表
	private boolean creatTB(String tableName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20) NOT NULL, number CHAR(15))");
		db.execSQL("INSERT OR IGNORE INTO current_tables VALUES(?)",
				new Object[] { tableName });
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return true;
	}

	/**
	 * 删除一张表
	 */
	public boolean cleanTB(String tableName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		db.execSQL("DROP TABLE " + tableName);
		db.execSQL("DELETE FROM current_tables WHERE name=?",
				new Object[] { tableName });
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return true;
	}

	public boolean insert(String table, List<Person> persons) {
		creatTB(table);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + table);
		for (Person person : persons) {
			db.execSQL("INSERT INTO " + table + "(name,number) VALUES(?,?)",
					new Object[] { person.getName(), person.getNumber() });
		}
		db.close();
		return true;
	}

	public List<Person> queryforPersons(String table, String condition) {
		SQLiteDatabase db = helper.getReadableDatabase();
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT name, number FROM ");
		sqlBuilder.append(table);
		sqlBuilder.append(" WHERE name LIKE '%");
		sqlBuilder.append(condition);
		sqlBuilder.append("%'");
		Cursor c = db.rawQuery(sqlBuilder.toString(), null);
		List<Person> data = new ArrayList<Person>();
		while (c.moveToNext()) {
			Person person = new Person(c.getString(0), c.getString(1));
			data.add(person);
		}
		c.close();
		db.close();
		return data;
	}

}