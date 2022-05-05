package com.mongodb.starter.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
public class ApplicationUser {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String username;
    private String password;
    private int mobile;
    private String description="";



    public String getDescription() {
        return description;
    }

    public ApplicationUser setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser that = (ApplicationUser) o;
        return mobile == that.mobile && id.equals(that.id) && username.equals(that.username) && password.equals(that.password) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, mobile, description);
    }

    public int getMobile() {
        return mobile;
    }

    public ApplicationUser setMobile(int mobile) {
        this.mobile = mobile;
        return this;
    }


    public ObjectId getId() {
        return id;
    }

    public ApplicationUser setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ApplicationUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mobile=" + mobile +
                ", description='" + description + '\'' +
                '}';
    }

    public ApplicationUser setPassword(String password) {
        this.password = password;
        return this;
    }
}
