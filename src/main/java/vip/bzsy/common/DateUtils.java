package vip.bzsy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lyf
 * @create 2019-03-27 13:04
 */
public class DateUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");

    public static Long getNowStartDateTime(Long time){
        Date now = new Date(time);
        String format = simpleDateFormat.format(now);
        try {
            now = simpleDateFormat.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return now.getTime();
    }

    public static Long getNestDayStartDateTime(Long time){
        Long oneday = 24*60*60*1000L;
        return getNowStartDateTime(time)+oneday;
    }

    public static void main(String[] args) {
        System.out.println(getNestDayStartDateTime(new Date().getTime()));
    }
}
