package zy18735.example.deliveryMaster.dal.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import zy18735.example.deliveryMaster.dal.db.DBHelper;

public class MyProvider extends ContentProvider
{
    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "user", 1);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "user/#", 2);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "orders", 3);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "orders/#", 4);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "*", 5);
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new DBHelper(this.getContext(), "mydb", null, 6);
        return true;
    }

    @Override
    public String getType(Uri uri) {

        String contentType;

        if (uri.getLastPathSegment()==null)
        {
            contentType = MyProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else
        {
            contentType = MyProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri))
        {
            case 1:
                tableName = "user";
                break;
            case 3:
                tableName = "orders";
                break;
                // add more table names
            default:
                tableName = "user";
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);

        getContext().getContentResolver().notifyChange(nu, null);

        return nu;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri))
        {
            case 2:
                selection = "_id = " + uri.getLastPathSegment();
            case 1:
                return db.query("user", projection, selection, selectionArgs, null, null, sortOrder);
            case 4:
                selection = "_id = " + uri.getLastPathSegment();
            case 3:
                return db.query("orders", projection, selection, selectionArgs, null, null, sortOrder);
            case 5:
                String q1 = "SELECT * FROM test";
                return db.rawQuery(q1, selectionArgs);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri))
        {
            case 2:
                selection = "_id = " + uri.getLastPathSegment();
            case 1:
                return db.update("user", values, selection, selectionArgs);
            case 4:
                selection = "_id = " + uri.getLastPathSegment();
            case 3:
                return db.update("orders", values, selection, selectionArgs);
            default:
                return -1;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }

}