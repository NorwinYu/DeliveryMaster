package zy18735.example.deliveryMaster.dal.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create table user

        db.execSQL("CREATE TABLE user (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(255)," +
                "email VARCHAR(255)," +
                "pass VARCHAR(64)," +
                "type INTEGER," +
                "order_number INTEGER," +
                "price_sum FLOAT," +
                "distance_sum FLOAT," +
                "status INTEGER," +
                "cur_order_id INTEGER" +
                ");");

        // Create table orders

        db.execSQL("CREATE TABLE orders (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title VARCHAR(128)," +
                "detail VARCHAR(500)," +
                "pickup_latitude FLOAT," +
                "pickup_longitude FLOAT," +
                "delivery_latitude FLOAT," +
                "delivery_longitude FLOAT," +
                "status INTEGER," +
                "deliveryman_id INTEGER," +
                "deliveryman_name VARCHAR(255)," +
                "price FLOAT," +
                "distance FLOAT" +
                ");");

        // Add some default values

        // Add default user
        db.execSQL("INSERT INTO user " +
                "(name, email, pass, type, order_number, price_sum, distance_sum, status, cur_order_id)" +
                " VALUES " +
                "('Norwin', 'norwinyu@gmail.com', 'Passfornorwin', 0, 0, 0.0, 0.0, 0, -1);"
        );



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop old table and call create method

        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

}