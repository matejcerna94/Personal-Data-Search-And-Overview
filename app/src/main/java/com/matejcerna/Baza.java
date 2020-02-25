package com.matejcerna;

public class Baza {

   /*  private static final String DB_URL = "jdbc:mysql://remotemysql.com:3306/3kPehvOr6E?useUnicode=true&characterEncoding=utf-8";
    private static final String USER = "3kPehvOr6E";
    private static final String PASS = "d39tMhGRI4";*/

    private static final String DB_URL = "jdbc:mysql://192.168.70.33:3306/osobe_diplomski?characterEncoding=utf-8";
    private static final String USER = "matej";
    private static final String PASS = "matej";

    public Baza() {
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASS() {
        return PASS;
    }


}


