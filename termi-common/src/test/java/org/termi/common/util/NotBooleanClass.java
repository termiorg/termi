package org.termi.common.util;

import java.time.LocalDate;
import java.util.Map;

public class NotBooleanClass {
    byte byte1 = Byte.MIN_VALUE;
    Byte byte2 = Byte.MAX_VALUE;
    short short1 = Short.MIN_VALUE;
    Short short2 = Short.MAX_VALUE;
    int int1 = Integer.MIN_VALUE;
    Integer int2 = Integer.MAX_VALUE;
    long long1 = Long.MIN_VALUE;
    Long long2 = Long.MAX_VALUE;
    float float1 = Float.MIN_VALUE;
    Float float2 = Float.MAX_VALUE;
    double double1 = Double.MIN_VALUE;
    Double double2 = Double.MAX_VALUE;
    char char1 = Character.MIN_VALUE;
    Character char2 = Character.MAX_VALUE;
    Map<String, String> map1 = Map.of("a", "b");
    LocalDate localDate = LocalDate.MAX;
}
