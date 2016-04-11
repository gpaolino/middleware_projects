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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "UPLOADSESSION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Uploadsession.findAll", query = "SELECT u FROM Uploadsession u"),
    @NamedQuery(name = "Uploadsession.findById", query = "SELECT u FROM Uploadsession u WHERE u.id = :id"),
    @NamedQuery(name = "Uploadsession.findByStatus", query = "SELECT u FROM Uploadsession u WHERE u.status = :status"),
    @NamedQuery(name = "Uploadsession.findByImg1", query = "SELECT u FROM Uploadsession u WHERE u.img1 = :img1"),
    @NamedQuery(name = "Uploadsession.findByImg2", query = "SELECT u FROM Uploadsession u WHERE u.img2 = :img2"),
    @NamedQuery(name = "Uploadsession.findByImg3", query = "SELECT u FROM Uploadsession u WHERE u.img3 = :img3"),
    @NamedQuery(name = "Uploadsession.findByImg4", query = "SELECT u FROM Uploadsession u WHERE u.img4 = :img4"),
    @NamedQuery(name = "Uploadsession.findByIsCrop", query = "SELECT u FROM Uploadsession u WHERE u.isCrop = :isCrop"),
    @NamedQuery(name = "Uploadsession.findByIsGreyScale", query = "SELECT u FROM Uploadsession u WHERE u.isGreyScale = :isGreyScale"),
    @NamedQuery(name = "Uploadsession.findByUploaded", query = "SELECT u FROM Uploadsession u WHERE u.uploaded = :uploaded")})
public class Uploadsession implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "status")
    private Integer status;
    @Size(max = 120)
    @Column(name = "img1")
    private String img1;
    @Size(max = 120)
    @Column(name = "img2")
    private String img2;
    @Size(max = 120)
    @Column(name = "img3")
    private String img3;
    @Size(max = 120)
    @Column(name = "result")
    private String result;
    @Size(max = 120)
    @Column(name = "img4")
    private String img4;
    @Column(name = "isCrop")
    private Boolean isCrop;
    @Column(name = "isGreyScale")
    private Boolean isGreyScale;
    @Column(name = "uploaded")
    private Integer uploaded;

    public Uploadsession() {
    }

    public Uploadsession(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }
    
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public Boolean getIsCrop() {
        return isCrop;
    }

    public void setIsCrop(Boolean isCrop) {
        this.isCrop = isCrop;
    }

    public Boolean getIsGreyScale() {
        return isGreyScale;
    }

    public void setIsGreyScale(Boolean isGreyScale) {
        this.isGreyScale = isGreyScale;
    }

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
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
        if (!(object instanceof Uploadsession)) {
            return false;
        }
        Uploadsession other = (Uploadsession) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.middleware.model.Uploadsession[ id=" + id + " ]";
    }
    
}
