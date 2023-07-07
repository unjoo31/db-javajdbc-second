package util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MyStringUtils {

    public static boolean containsKorean(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    public static String dateFormat(Timestamp transferDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String formattedDate = dateFormat.format(transferDate);
        return formattedDate;
    }

}
