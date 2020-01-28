package zy18735.example.deliveryMaster.dal.provider;
import android.net.Uri;

public class MyProviderContract
{
    // Provider Class Path

    public static final String AUTHORITY = "zy18735.example.deliveryMaster.dal.provider.MyProvider";

    // URIs
    public static final Uri USER_URI = Uri.parse("content://"+AUTHORITY+"/user");
    public static final Uri ORDERS_URI = Uri.parse("content://"+AUTHORITY+"/orders");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String _ID = "_id";

    // TABLE USER

    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASS = "pass";
    public static final String USER_TYPE = "type";
    public static final String USER_ORDER_NUMBER = "order_number";
    public static final String USER_PRICE_SUM = "price_sum";
    public static final String USER_DISTANCE_SUM = "distance_sum";
    public static final String USER_STATUS = "status";
    public static final String USER_CUR_ORDER_ID = "cur_order_id";

    // TABLE ORDERS

    public static final String ORDERS_TITLE = "title";
    public static final String ORDERS_DETAIL = "detail";
    public static final String ORDERS_PICKUP_LATITUDE = "pickup_latitude";
    public static final String ORDERS_PICKUP_LONGITUDE = "pickup_longitude";
    public static final String ORDERS_DELIVERY_LATITUDE = "delivery_latitude";
    public static final String ORDERS_DELIVERY_LONGITUDE = "delivery_longitude";
    public static final String ORDERS_STATUS = "status";
    public static final String ORDERS_DELIVERYMAN_ID = "deliveryman_id";
    public static final String ORDERS_DELIVERYMAN_NAME = "deliveryman_name";
    public static final String ORDERS_PRICE = "price";
    public static final String ORDERS_DISTANCE = "distance";

    // CONTENT TYPE

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyProvider.data.text";
}