package com.dev.baqari.floating_window_button;


public class PointUtil {

    public static int width;
    public static int height;

    private static int insideRadius = 300;

    public static boolean isInsidePoint(float x, float y) {
        boolean result = false;

        int x1, y1, x2, y2;
        x1 = (width / 2) + (insideRadius + 10);
        y1 = height - (2 * insideRadius);
        x2 = (width / 2) + (insideRadius + 20);
        y2 = height - (insideRadius - 100);

        if (x1 < x && y1 < y)
            result = true;
        else if (x2 > x && y2 > y)
            result = true;

        return result;
    }
}
