package com.enpm809w.mims.Common;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.net.URL;
import java.sql.Timestamp;

@Entity
@Table(name = "insurances")
public class Insurance {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    @NotNull
    @Column(name = "insurance_name")
    private String insuranceName;

    @NotNull
    @Column(name = "insurance_type")
    private String insuranceType;

    @NotNull
    @Column(name = "insurance_Period")
    private String insurancePeriod;

    @NotNull
    @Column(name = "insurance_Link")
    private String insuranceLink;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsurancePeriod() {
        return insurancePeriod;
    }

    public void setInsurancePeriod(String insurancePeriod) {
        this.insurancePeriod = insurancePeriod;
    }
    public String getInsuranceLink() {
        return insuranceLink;
    }

    public void setInsuranceLink(String insuranceLink) {
        this.insuranceLink = insuranceLink;
    }

}
