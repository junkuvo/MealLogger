package junkuvo.apps.meallogger.view;

public class ListRow {
    private static final String TAG = ListRow.class.getSimpleName();
    private final ListRow self = this;

    private String mealMenuTitle;
    private int mealMenuThumbnail;


    public String getMealMenuTitle() {
        return mealMenuTitle;
    }

    public void setMealMenuTitle(String mealMenuTitle) {
        this.mealMenuTitle = mealMenuTitle;
    }

    public int getMealMenuThumbnail() {
        return mealMenuThumbnail;
    }

    public void setMealMenuThumbnail(int mealMenuThumbnail) {
        this.mealMenuThumbnail = mealMenuThumbnail;
    }
}
