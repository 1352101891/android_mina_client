package com.lq.im.util;

import java.util.List;

public class  SafeCheck{

    public static String s(String s){
        if (s==null || s.length()==0){
            return "";
        }else {
            return s;
        }
    }

    public static boolean isNull(String s){
        if (s==null || s.length()==0){
            return true;
        }
        return false;
    }

    public static String replace(String s,String defaultStr){
        if (s==null || s.length()==0){
            return defaultStr;
        }
        return s;
    }

    public static boolean isListEmpty(List list){
        if (list==null || list.size()==0){
            return true;
        }
        return false;
    }

    public static boolean isListValide(List list){
        if (list==null || list.size()==0){
            return false;
        }
        return true;
    }
}
