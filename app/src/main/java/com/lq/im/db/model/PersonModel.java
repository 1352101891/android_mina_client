package com.lq.im.db.model;

import java.io.Serializable;

public class PersonModel implements Serializable {
    private static final long serialVersionUID = 12L;
    public long id;
    public String Name;
    public int age;
    public int isBoy;
    public String address;
    public String pic;
    public String username;
    public String password;

    public PersonModel() {
    }

    public PersonModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public int getIsBoy() {
        return isBoy;
    }

    public void setIsBoy(int isBoy) {
        this.isBoy = isBoy;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
