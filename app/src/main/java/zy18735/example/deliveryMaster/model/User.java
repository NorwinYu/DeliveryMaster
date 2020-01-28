package zy18735.example.deliveryMaster.model;

import android.content.ContentValues;

import zy18735.example.deliveryMaster.dal.provider.MyProviderContract;

public class User {

    private Integer _id;
    private String name;
    private String email;
    private String pass;
    private Integer type;
    private Integer order_number;
    private float price_sum;
    private float distance_sum; // km
    private Integer status;  // 0. rest 1. delivering
    private Integer cur_order_id;

    public User() {
    }

    public User(Integer _id, String name, String email, String pass, Integer type, Integer order_number, float price_sum, float distance_sum, Integer status, Integer cur_order_id) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.type = type;
        this.order_number = order_number;
        this.price_sum = price_sum;
        this.distance_sum = distance_sum;
        this.status = status;
        this.cur_order_id = cur_order_id;
    }

    public ContentValues toContentValuesStatusDelivered() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract.USER_STATUS, this.status);
        contentValues.put(MyProviderContract.USER_CUR_ORDER_ID, this.cur_order_id);
        contentValues.put(MyProviderContract.USER_ORDER_NUMBER, this.order_number);
        contentValues.put(MyProviderContract.USER_DISTANCE_SUM, this.distance_sum);
        contentValues.put(MyProviderContract.USER_PRICE_SUM, this.price_sum);
        return  contentValues;
    }

    public ContentValues toContentValuesStatusAndOrder() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyProviderContract.USER_STATUS, this.status);
        contentValues.put(MyProviderContract.USER_CUR_ORDER_ID, this.cur_order_id);
        return  contentValues;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrder_number() {
        return order_number;
    }

    public void setOrder_number(Integer order_number) {
        this.order_number = order_number;
    }

    public float getPrice_sum() {
        return price_sum;
    }

    public void setPrice_sum(float price_sum) {
        this.price_sum = price_sum;
    }

    public float getDistance_sum() {
        return distance_sum;
    }

    public void setDistance_sum(float distance_sum) {
        this.distance_sum = distance_sum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCur_order_id() {
        return cur_order_id;
    }

    public void setCur_order_id(Integer cur_order_id) {
        this.cur_order_id = cur_order_id;
    }
}
