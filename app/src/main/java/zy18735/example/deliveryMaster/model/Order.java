package zy18735.example.deliveryMaster.model;

import android.content.ContentValues;

import zy18735.example.deliveryMaster.dal.provider.MyProvider;
import zy18735.example.deliveryMaster.dal.provider.MyProviderContract;

public class Order {

    private Integer _id;
    private String title;
    private String detail;
    private Float pickup_latitude;
    private Float pickup_longitude;
    private Float delivery_latitude;
    private Float delivery_longitude;
    private Integer status; // -- 0. haven't be chosen 1. price successfully set 2. pending pickup 3. picked-up 4. delivered
    private Integer deliveryman_id;
    private String deliveryman_name;
    private Float price;
    private Float distance; //  -- km

    public Order() {
    }

    public Order(Integer _id, String title, String detail, Float pickup_latitude, Float pickup_longitude, Float delivery_latitude, Float delivery_longitude, Integer status, Integer deliveryman_id, String deliveryman_name, Float price, Float distance) {
        this._id = _id;
        this.title = title;
        this.detail = detail;
        this.pickup_latitude = pickup_latitude;
        this.pickup_longitude = pickup_longitude;
        this.delivery_latitude = delivery_latitude;
        this.delivery_longitude = delivery_longitude;
        this.status = status;
        this.deliveryman_id = deliveryman_id;
        this.deliveryman_name = deliveryman_name;
        this.price = price;
        this.distance = distance;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract._ID, this._id);
        contentValues.put(MyProviderContract.ORDERS_TITLE, this.title);
        contentValues.put(MyProviderContract.ORDERS_DETAIL, this.detail);
        contentValues.put(MyProviderContract.ORDERS_PICKUP_LATITUDE, this.pickup_latitude);
        contentValues.put(MyProviderContract.ORDERS_PICKUP_LONGITUDE, this.pickup_longitude);
        contentValues.put(MyProviderContract.ORDERS_DELIVERY_LATITUDE, this.delivery_latitude);
        contentValues.put(MyProviderContract.ORDERS_DELIVERY_LONGITUDE, this.delivery_longitude);
        contentValues.put(MyProviderContract.ORDERS_STATUS, this.status);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_ID, this.deliveryman_id);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_NAME, this.deliveryman_name);
        contentValues.put(MyProviderContract.ORDERS_PRICE, this.price);
        contentValues.put(MyProviderContract.ORDERS_DISTANCE, this.distance);
        return  contentValues;
    }

    public ContentValues toContentValuesWithoutID() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract.ORDERS_TITLE, this.title);
        contentValues.put(MyProviderContract.ORDERS_DETAIL, this.detail);
        contentValues.put(MyProviderContract.ORDERS_PICKUP_LATITUDE, this.pickup_latitude);
        contentValues.put(MyProviderContract.ORDERS_PICKUP_LONGITUDE, this.pickup_longitude);
        contentValues.put(MyProviderContract.ORDERS_DELIVERY_LATITUDE, this.delivery_latitude);
        contentValues.put(MyProviderContract.ORDERS_DELIVERY_LONGITUDE, this.delivery_longitude);
        contentValues.put(MyProviderContract.ORDERS_STATUS, this.status);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_ID, this.deliveryman_id);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_NAME, this.deliveryman_name);
        contentValues.put(MyProviderContract.ORDERS_PRICE, this.price);
        contentValues.put(MyProviderContract.ORDERS_DISTANCE, this.distance);
        return  contentValues;
    }

    public ContentValues toContentValuesWithPrice() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract.ORDERS_PRICE, this.price);
        contentValues.put(MyProviderContract.ORDERS_STATUS, this.status);
        return  contentValues;
    }

    public ContentValues toContentValuesWithDeliverMan() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract.ORDERS_STATUS, this.status);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_ID, this.deliveryman_id);
        contentValues.put(MyProviderContract.ORDERS_DELIVERYMAN_NAME, this.deliveryman_name);
        return  contentValues;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Float getPickup_latitude() {
        return pickup_latitude;
    }

    public void setPickup_latitude(Float pickup_latitude) {
        this.pickup_latitude = pickup_latitude;
    }

    public Float getPickup_longitude() {
        return pickup_longitude;
    }

    public void setPickup_longitude(Float pickup_longitude) {
        this.pickup_longitude = pickup_longitude;
    }

    public Float getDelivery_latitude() {
        return delivery_latitude;
    }

    public void setDelivery_latitude(Float delivery_latitude) {
        this.delivery_latitude = delivery_latitude;
    }

    public Float getDelivery_longitude() {
        return delivery_longitude;
    }

    public void setDelivery_longitude(Float delivery_longitude) {
        this.delivery_longitude = delivery_longitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeliveryman_id() {
        return deliveryman_id;
    }

    public void setDeliveryman_id(Integer deliveryman_id) {
        this.deliveryman_id = deliveryman_id;
    }

    public String getDeliveryman_name() {
        return deliveryman_name;
    }

    public void setDeliveryman_name(String deliveryman_name) {
        this.deliveryman_name = deliveryman_name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
