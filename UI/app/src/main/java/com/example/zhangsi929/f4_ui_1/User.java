package com.example.zhangsi929.f4_ui_1;

/**
 * Created by Vencci on 03/04/2017.
 */

public class User {
    public static String username;
    public static String url;
    private String name;
    private String password;
    private String phone;
    private String email;

    //send register request to back end
    public void register(){
        url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/accounts/signup";
    }

    //send login request to backend
    public void login(){
        url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/accounts/login";
    }

    //send found/lost report to backend
    public void foundreports(){
        url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/found";
    }

    public void lostreports(){
        url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/lost";
    }

    //send query request to backend
    public void query(){
        url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/query/?name=";
    }

}
