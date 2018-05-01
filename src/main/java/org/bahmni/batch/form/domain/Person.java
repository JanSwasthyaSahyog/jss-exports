package org.bahmni.batch.form.domain;

import java.util.Date;

public class Person {
    private Integer id;
    private String name;
    private Date birthDate;
    private Integer age;
    private String gender;
    private String dead;
    private String deathDate;
    private String causeOfDeath;
    private Address address;
    private String smartCardHolder;
    private String smoking;
    private String alcohol;
    private String foodSecurity;
    private Integer familyIncome;

    public Person() {

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

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getFoodSecurity() {
        return foodSecurity;
    }

    public void setFoodSecurity(String foodSecurity) {
        this.foodSecurity = foodSecurity;
    }

    public Integer getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(Integer familyIncome) {
        this.familyIncome = familyIncome;
    }

    public String getDead() { return dead; }

    public void setDead(String dead) { this.dead = dead; }

    public String getDeathDate() { return deathDate; }

    public void setDeathDate(String deathDate) { this.deathDate = deathDate; }

    public String getCauseOfDeath() { return causeOfDeath; }

    public void setCauseOfDeath(String causeOfDeath) { this.causeOfDeath = causeOfDeath; }

}
