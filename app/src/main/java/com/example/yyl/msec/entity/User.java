package com.example.yyl.msec.entity;

/**
 * Created by YYL on 2016/10/28.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String passwordagain;
    private String mail;
    private String sex;
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String  username)
    {
        this.username=username;
    }
    public String getPasswordagain(){
        return passwordagain;
    }
    public void setPasswordagain(String passwordagain) {
        this.passwordagain = passwordagain;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMail()
    {
        return mail;
    }
public void setMail(String mail)
{
    this.mail=mail;
}
    public void setId(int id)
    {
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex=sex;
    }
    public User(){

    }
    public User(String username,String password,String mail,String sex)
    {
        this.username=username;
        this.password=password;
        this.mail=mail;
        this.sex=sex;
    }

    public String userCreateTable()
    {
        return   "CREATE TABLE "
                +"user"+getUsername()+"("
          + "id SMALLINT PRIMARY KEY AUTO_INCREMENT,"
          +"username  VARCHAR(30),"
          +"password VARCHAR(30),"
          +"mail    VARCHAR(100)"
          +")";

//        return "CREATE TABLE user(id SMALLINT PRIMARY KEY AUTO_INCREMENT,username VARCHAR(30),"
//                +"password VARCHAR(30),mail    VARCHAR(30),"
//                ;
    }

    public String userInsertIntoMysql()
    {
        return "insert into "
                +"users"
                +"(username,password,mail,sex)"
                +"values("
                +"\""+username+"\""+","
                +"\""+password+"\""+","
                +"\""+mail+"\""+","
                +"\""+sex+"\""
                +")";
    }





}
