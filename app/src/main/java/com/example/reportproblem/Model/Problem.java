package com.example.reportproblem.Model;

import java.time.LocalDateTime;
import java.util.Date;

public class Problem {

    private String id, description,location,nameOfThePlace,img;
    private int severity;
    private String creatorUser;
    private Type type;
    private String creatorName;

    private Date createdAt=new Date();

    public Problem(String id, String description, String location, String nameOfThePlace,
                   String img, int severity, String creatorUser, Type type, Date createdAt,String creatorName) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.nameOfThePlace = nameOfThePlace;
        this.img = img;
        this.severity = severity;
        this.creatorUser = creatorUser;
        this.type = type;
        this.createdAt = createdAt;
        this.creatorName=creatorName;

    }


    public Problem(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNameOfThePlace() {
        return nameOfThePlace;
    }

    public void setNameOfThePlace(String nameOfThePlace) {
        this.nameOfThePlace = nameOfThePlace;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", nameOfThePlace='" + nameOfThePlace + '\'' +
                ", img='" + img + '\'' +
                ", severity=" + severity +
                ", creatorUser='" + creatorUser + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                '}';
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
