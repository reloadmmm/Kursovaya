package com.example.kursovaya.data;

import androidx.room.TypeConverter;
import java.util.Date;

public class RoomConverters {
    @TypeConverter
    public static Long fromDate(Date d) {
        return d == null ? null : d.getTime();
    }

    @TypeConverter
    public static Date toDate(Long ms) {
        return ms == null ? null : new Date(ms);
    }
}
