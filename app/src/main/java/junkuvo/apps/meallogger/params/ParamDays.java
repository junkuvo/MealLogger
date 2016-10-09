package junkuvo.apps.meallogger.params;

public enum ParamDays {
    /**
     * 列挙定数の定義
     */
    SUN("日", 1),
    MON("月", 2),
    TUE("火", 3),
    WED("水", 4),
    THU("木", 5),
    FRI("金", 6),
    SAT("土", 7);

    /**
     * フィールド変数
     */
    private String label;
    private int value;

    /**
     * コンストラクタ
     */
    ParamDays(String label, int value) {
        this.label = label;
        this.value = value;
    }

    /**
     * 名称取得メソッド
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * 値取得メソッド
     */
    public int getValue() {
        return this.value;
    }
}
