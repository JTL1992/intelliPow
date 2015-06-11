package com.harmazing.intelligentpow.tools;

/**
 * Created by JTL on 2014/8/7.
 * 服务器头
 */
public class HttpHead {
//    public static String head = "http://192.168.1.101:8089/spmsuc/app/";
//    public static String head = "http://112.126.68.80:8280/spmsuc/app/";
//    public static String head = "http://192.168.1.92:8089/spmsuc/app/";
//    public static String head = "http://192.168.1.108:8089/spmsuc/app/";
//    public static String head = "http://123.56.88.237:8080/spms/app/";
      public static String forhead = "112.126.68.80:8080";
      public static String head = "http://112.126.68.80:8080/spms/app/";
//    public static String head = "http://192.168.20.107:8080/spms/app/";
      public static String secHead = "http://"+forhead+"/spms/resetPassword/doSendEmailMobile";
      public static void setHead(String newHead){
         head = "http://"+ newHead + "/spms/app/";
         forhead = newHead;
         secHead = "http://"+forhead+"/spms/resetPassword/doSendEmailMobile";
      }
//      public static String getHead(){
//          return  head.substring(7,25);
//      }

}
