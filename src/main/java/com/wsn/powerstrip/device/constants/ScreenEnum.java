package com.wsn.powerstrip.device.constants;

public enum ScreenEnum {
    SCREEN1(8, 16),
    SCREEN2(12, 19),
    SCREEN3(7, 14),
    OTHERS(0, 0);
    private static final ScreenEnum[] SCREENS = {OTHERS, SCREEN1, SCREEN2, SCREEN3};
    private final Integer rows;
    private final Integer cols;

    ScreenEnum(Integer rows, Integer cols) {
        this.rows = rows;
        this.cols = cols;
    }
    public int getRows(){
        return rows;
    }
    public int getCols(){
        return cols;
    }
    public static ScreenEnum select(int num){
        return SCREENS[num];
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    public static int count(){
        return SCREENS.length;
    }
}
