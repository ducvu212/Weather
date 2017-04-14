package com.example.vuminhduc.weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    private static final String Key ="34e634abc7ab97968d2d830d16578847" ;
    private static final String Link ="http://api.openweathermap.org/data/2.5/weather";

    public static String WeatherLink (String lat, String lon) {

        StringBuilder stringBuilder = new StringBuilder(Link);
        stringBuilder.append(String.format("?lat=%s&lon=%s&APPID=%s", lat, lon, Key));

        return stringBuilder.toString();
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static String convertTime(double time) {
        DateFormat date = new SimpleDateFormat("HH:mm") ;
        Date date1 = new Date() ;
        date1.setTime((long) time * 1000);
        return date.format(date1);
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm") ;
        Date date = new Date() ;
        return dateFormat.format(date) ;
    }

}
