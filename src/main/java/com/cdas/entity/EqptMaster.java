package com.cdas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "eqpt_master")
public class EqptMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_code")
    private String shopCode;

    @Column(name = "shop_desc")
    private String shopDesc;

    @Column(name = "eqpt_code")
    private String eqptCode;

    @Column(name = "sub_eqpt_code")
    private String subEqptCode;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc;
    }

    public String getEqptCode() {
        return eqptCode;
    }

    public void setEqptCode(String eqptCode) {
        this.eqptCode = eqptCode;
    }

    public String getSubEqptCode() {
        return subEqptCode;
    }

    public void setSubEqptCode(String subEqptCode) {
        this.subEqptCode = subEqptCode;
    }
}