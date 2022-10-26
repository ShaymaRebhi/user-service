package com.proxym.poleactualities.Payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proxym.poleactualities.Models.Role;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class UserProfile {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    private Instant joinedAt;


    private String address;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthdate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date hiringDate;

    private long phoneNumber;

    private Set<Role> role;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Date hiringDate) {
        this.hiringDate = hiringDate;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Role> getRole() {
        return role;
    }

    public void setRole(Set<Role> role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private Long voteCount;

    public UserProfile(Long id, String username, String firstName, String lastName, Instant joinedAt, String address, Date birthdate, Date hiringDate, Long phoneNumber, Set<Role> role ) {
        this.id = id;
        this.username = username;
        this.firstName =firstName;
        this.lastName = lastName;
        this.joinedAt = joinedAt;
        this.address = address;
        this.birthdate=birthdate;
        this.hiringDate=hiringDate;
        this.phoneNumber=phoneNumber;
        this.role= role;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }


}
