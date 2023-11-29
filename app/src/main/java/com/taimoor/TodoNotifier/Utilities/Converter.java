package com.taimoor.TodoNotifier.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import com.taimoor.TodoNotifier.Model.Priority;

import java.io.ByteArrayOutputStream;
import java.util.Date;

//Class to convert resources into different formats
public class Converter {

    //Converting time and date to required format
    @TypeConverter
    public static Date fromTimeStamp(Long value){
        return value ==  null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date){
        return date ==  null ? null : date.getTime();
    }

    //Get the String  representation of the enum fields
    @TypeConverter
    public static String fromPriority(Priority priority){
        return priority == null ? null : priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String priority){
        return priority == null ? null : Priority.valueOf(priority);
    }

    //Getting String resource from bitmap
    @TypeConverter
    public static byte[] getStringFromBitmap(Bitmap bitmapPicture){
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b =byteArrayBitmapStream.toByteArray();
        return b;
    }

    //Getting bitmap from String Resource
    @TypeConverter
    public static Bitmap getBitmapFromStr(byte[] arr){
        return BitmapFactory.decodeByteArray(arr, 0 , arr.length);
    }




}
