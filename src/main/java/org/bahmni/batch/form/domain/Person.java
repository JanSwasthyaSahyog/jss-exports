package org.bahmni.batch.form.domain;

import java.util.Date;

public class Person {
    private Integer id;
    private String name;
    private Date birthDate;
    private Integer age;
    private String gender;
    private Address address;
    private String smartCardHolder;

    public Person(){

    }

    public Person(Integer id, String name, Date birthDate, Integer age, String gender, Address address) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public String getSmartCardHolder() {
        return smartCardHolder;
    }

    public void setSmartCardHolder(String smartCardHolder) {
        this.smartCardHolder = smartCardHolder;
    }
}
