package com.example.luist.munidenuncias.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Post {

    private String id;

    private String userid;

    private String title;

    private String body;

    private String photoUrl;

    private Map<String, Boolean> view = new HashMap<>();

    private Double latitude;

    private Double longitude;

    private String direccion;

    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Map<String, Boolean> getView() {
        return view;
    }

    public void setView(Map<String, Boolean> view) {
        this.view = view;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", userid='" + userid + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", view=" + view +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", direccion='" + direccion + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

}

