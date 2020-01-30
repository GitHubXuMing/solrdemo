package com.spring.boot.solrdemo;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;
@SolrDocument(solrCoreName = "city_core")
public class City implements Serializable {
    @Id
    @Field
    private int cid;
    @Field
    private String city;
    @Field
    private int pid;

    @Override
    public String toString() {
        return "City{" +
                "cid=" + cid +
                ", city='" + city + '\'' +
                ", pid=" + pid +
                '}';
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public City(int cid, String city, int pid) {
        this.cid = cid;
        this.city = city;
        this.pid = pid;
    }

    public City() {
    }
}
