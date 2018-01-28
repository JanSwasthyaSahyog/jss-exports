package org.bahmni.batch.form.domain;

public class Address {
    private String village;
    private String block;
    private String tehsil;
    private String district;

    public Address(String village, String block, String tehsil, String district) {
        this.village = village;
        this.block = block;
        this.tehsil = tehsil;
        this.district = district;
    }

    public String getVillage() {
        return village;
    }


    public String getBlock() {
        return block;
    }


    public String getTehsil() {
        return tehsil;
    }


    public String getDistrict() {
        return district;
    }

}
