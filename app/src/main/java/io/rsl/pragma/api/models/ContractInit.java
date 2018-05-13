package io.rsl.pragma.api.models;

import io.rsl.pragma.db.dbmodels.DBContract;

public class ContractInit {

    private int type;
    private String title;
    private String data;
    private int status;

    public ContractInit(int type, String title, String data, int status) {
        this.type = type;
        this.title = title;
        this.data = data;
        this.status = status;
    }

    public ContractInit(DBContract contract) {
        this.type = contract.getType();
        this.title = contract.getTitle();
        this.data = contract.getData();
        this.status = contract.getStatus();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
