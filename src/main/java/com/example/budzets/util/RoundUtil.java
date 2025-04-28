package com.example.budzets.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundUtil {

    public static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static double roundQuantity(double value) {
        return BigDecimal.valueOf(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
