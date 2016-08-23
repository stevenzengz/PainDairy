package monash.fit5046.assign.assignmentpaindiary.BusinessLogic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Steven on 26/04/2016.
 */
public class DatabaseHepler extends SQLiteOpenHelper {

    // Query of create Record table
    public static final String CREATE_RECORD = "CREATE TABLE Record (" +
            "userid text, " +
            "address text, " +
            "latitude text, " +
            "longitude text, " +
            "date text, " +
            "time text)";

    // Query of create Paintrigger table
    public static final String CREATE_PAINTRIGGER = "CREATE TABLE Paintrigger (" +
            "userid text, " +
            "activity text)";

    private Context dbContext;

    public DatabaseHepler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_PAINTRIGGER);
        Toast.makeText(dbContext, "Record table created succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Record");
        db.execSQL("DROP TABLE IF EXISTS Paintrigger");
        onCreate(db);
    }
}
