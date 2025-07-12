package me.trusha.fms;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String id;
    private double amount;
    private String date;
    private String description;
    private String category;
    private String type;

    public Transaction() {}

    public Transaction(String id, double amount, String date, String description, String category, String type) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        amount = in.readDouble();
        date = in.readString();
        description = in.readString();
        category = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(amount);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(type);
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
