/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.middleware.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author andreagulino
 */
@Entity
@Table(name = "CONSUMER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Consumer.findAll", query = "SELECT c FROM Consumer c"),
    @NamedQuery(name = "Consumer.findById", query = "SELECT c FROM Consumer c WHERE c.id = :id"),
    @NamedQuery(name = "Consumer.findByAppName", query = "SELECT c FROM Consumer c WHERE c.appName = :appName"),
    @NamedQuery(name = "Consumer.findByOauthConsumerKey", query = "SELECT c FROM Consumer c WHERE c.oauthConsumerKey = :oauthConsumerKey"),
    @NamedQuery(name = "Consumer.findByOauthSignature", query = "SELECT c FROM Consumer c WHERE c.oauthSignature = :oauthSignature"),
    @NamedQuery(name = "Consumer.findByOauthCallback", query = "SELECT c FROM Consumer c WHERE c.oauthCallback = :oauthCallback"),
    @NamedQuery(name = "Consumer.findByUser", query = "SELECT c FROM Consumer c WHERE c.user = :user")})
public class Consumer implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user")
    private int user;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "app_name")
    private String appName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "oauth_consumer_key")
    private String oauthConsumerKey;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "oauth_signature")
    private String oauthSignature;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "oauth_callback")
    private String oauthCallback;

    public Consumer() {
    }

    public Consumer(Integer id) {
        this.id = id;
    }

    public Consumer(Integer id, String appName, String oauthConsumerKey, String oauthSignature, String oauthCallback) {
        this.id = id;
        this.appName = appName;
        this.oauthConsumerKey = oauthConsumerKey;
        this.oauthSignature = oauthSignature;
        this.oauthCallback = oauthCallback;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getOauthConsumerKey() {
        return oauthConsumerKey;
    }

    public void setOauthConsumerKey(String oauthConsumerKey) {
        this.oauthConsumerKey = oauthConsumerKey;
    }

    public String getOauthSignature() {
        return oauthSignature;
    }

    public void setOauthSignature(String oauthSignature) {
        this.oauthSignature = oauthSignature;
    }

    public String getOauthCallback() {
        return oauthCallback;
    }

    public void setOauthCallback(String oauthCallback) {
        this.oauthCallback = oauthCallback;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Consumer)) {
            return false;
        }
        Consumer other = (Consumer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.middleware.model.Consumer[ id=" + id + " ]";
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
    
}
