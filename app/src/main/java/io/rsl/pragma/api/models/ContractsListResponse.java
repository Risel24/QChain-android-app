package io.rsl.pragma.api.models;

import java.util.List;

import io.rsl.pragma.db.dbmodels.DBContract;

public class ContractsListResponse {

    private int              count;
    private List<DBContract> contracts;

    public ContractsListResponse(int count, List<DBContract> contracts) {
        this.count = count;
        this.contracts = contracts;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DBContract> getContracts() {
        return contracts;
    }

    public void setContracts(List<DBContract> contracts) {
        this.contracts = contracts;
    }
}
