package com.mongodb.starter.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

@JsonInclude(Include.NON_NULL)
public class Experience {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String username;

    private String imagePath;
    private String exp;

    public ObjectId getId() {
        return id;
    }

    public Experience setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Experience setUsername(String username) {
        this.username = username;
        return this;
    }


    public String getImagePath() {
        return imagePath;
    }

    public Experience setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getExp() {
        return exp;
    }

    public Experience setExp(String exp) {
        this.exp = exp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experience that = (Experience) o;
        return id.equals(that.id) && username.equals(that.username) && Objects.equals(imagePath, that.imagePath) && Objects.equals(exp, that.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, imagePath, exp);
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", exp='" + exp + '\'' +
                '}';
    }
}
