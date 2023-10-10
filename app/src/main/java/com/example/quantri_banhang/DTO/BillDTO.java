package com.example.quantri_banhang.DTO;

public class BillDTO {
    String idBill;
    String iduser;
    double TotalPrice;
    String dateBuy;
    int status;

    public BillDTO() {
    }

    public BillDTO(String idBill, String iduser, double totalPrice, String dateBuy, int status) {
        this.idBill = idBill;
        this.iduser = iduser;
        TotalPrice = totalPrice;
        this.dateBuy = dateBuy;
        this.status = status;
    }

    public String getIdBill() {
        return idBill;
    }

    public void setIdBill(String idBill) {
        this.idBill = idBill;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getDateBuy() {
        return dateBuy;
    }

    public void setDateBuy(String dateBuy) {
        this.dateBuy = dateBuy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
