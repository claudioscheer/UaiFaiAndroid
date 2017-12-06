package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import model.WifiSimpleDataSQLiteModel;

public class WifiSQLiteHelper extends SQLiteHelper {

    private final Gson gson = new Gson();

    public WifiSQLiteHelper(Context context) {
        super(context);
    }

    public void save(WifiSimpleDataSQLiteModel wifiSimpleDataSQLiteModel) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("json", gson.toJson(wifiSimpleDataSQLiteModel));

        db.insert("WIFI", null, values);
    }

    public List<WifiSimpleDataSQLiteModel> get() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("WIFI", new String[]{"id", "json"}, null, null, null, null, null);

        List<WifiSimpleDataSQLiteModel> wifiSimpleDataSQLiteModels = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            WifiSimpleDataSQLiteModel wifiSimpleDataSQLiteModel = gson.fromJson(cursor.getString(1), WifiSimpleDataSQLiteModel.class);
            wifiSimpleDataSQLiteModel.setId(id);
            wifiSimpleDataSQLiteModels.add(wifiSimpleDataSQLiteModel);
        }
        cursor.close();

        return wifiSimpleDataSQLiteModels;
    }

    public void delete(List<WifiSimpleDataSQLiteModel> wifiSimpleDataSQLiteModels) {
        if (wifiSimpleDataSQLiteModels.size() <= 0) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (WifiSimpleDataSQLiteModel wifiSimpleDataSQLiteModel : wifiSimpleDataSQLiteModels) {
            db.delete("WIFI", "id = ?", new String[]{wifiSimpleDataSQLiteModel.getId().toString()});
        }
    }
}
