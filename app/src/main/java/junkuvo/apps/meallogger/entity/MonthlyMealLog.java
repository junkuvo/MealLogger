package junkuvo.apps.meallogger.entity;


import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class MonthlyMealLog extends RealmObject {
    @PrimaryKey
    private long id;
    private long sumPrice;
    @Index
    private int month;
    @Index
    private int year;

    public MonthlyMealLog() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(long sumPrice) {
        this.sumPrice = sumPrice;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonthlyMealLog(int year, int month, long sumPrice){
        setId(System.currentTimeMillis());
        setSumPrice(sumPrice);
        setYear(year);
        setMonth(month);
    }
}