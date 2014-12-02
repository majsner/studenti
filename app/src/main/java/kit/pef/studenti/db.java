package kit.pef.studenti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jan on 1. 12. 2014.
 */
public class db extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "mydb";
    private static final String TABLE_NAME = "studenti";
    private static final String COL_NAME_JMENO = "jmeno"; // lepší deklarace jména sloupce
    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ("+COL_NAME_JMENO+" TEXT, prijmeni TEXT,id INT);";

    db(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {db.execSQL(SQL_CREATE);}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       onCreate(db);

    }

    public void insertStudenti(ArrayList<Student> students){
        for (Student student:students) {
            // použití čistého SQL dotazu by bylo rychlejší, zvláště u velkých objemů dat
            ContentValues values = new ContentValues();
            values.put(COL_NAME_JMENO,student.getJmeno());
            values.put("prijmeni",student.getPrijmeni());
            values.put("id",student.getId());
            getWritableDatabase().insert(TABLE_NAME,null,values);
        }
    }

    public ArrayList<Student> getStudenti(String orderBy) {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + orderBy;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        ArrayList<Student> dataOrdered = new ArrayList<Student>();
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(Integer.valueOf(cursor.getColumnIndex("id"))));
                student.setJmeno(cursor.getString(Integer.valueOf(cursor.getColumnIndex(COL_NAME_JMENO))));
                student.setPrijmeni(cursor.getString(Integer.valueOf(cursor.getColumnIndex("prijmeni"))));
                // Adding contact to list
                dataOrdered.add(student);
            } while (cursor.moveToNext());
        }
        return dataOrdered;
    }
}