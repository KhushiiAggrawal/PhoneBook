package com.example.phonebook;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {
    int id;
    String fname="",lname="",cname="",pnumber="",emailid="",dob="",uri="";
    boolean isFavourite =false;

    public Person(int id,String fname, String lname, String cname, String pnumber, String emailid, String dob,String uri) {
        this.id=id;
        this.fname = fname;
        this.lname = lname;
        this.cname = cname;
        this.pnumber = pnumber;
        this.emailid = emailid;
        this.dob = dob;
        this.uri=uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getCname() {
        return cname;
    }

    public String getPnumber() {
        return pnumber;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getDob() {
        return dob;
    }

    public String getUri() {
        return uri;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(pnumber, person.pnumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnumber);
    }

}
