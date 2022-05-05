package com.mongodb.starter.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

@JsonInclude(Include.NON_NULL)
public class PlacementMaterial {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String subject;
    private String link;

    public ObjectId getId() {
        return id;
    }

    public PlacementMaterial setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public PlacementMaterial setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getLink() {
        return link;
    }

    public PlacementMaterial setLink(String link) {
        this.link = link;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlacementMaterial that = (PlacementMaterial) o;
        return id.equals(that.id) && subject.equals(that.subject) && link.equals(that.link);
    }

    @Override
    public String toString() {
        return "PlacementMaterial{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, link);
    }
}
