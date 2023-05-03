package com.wxh.shareiot.api;

import java.util.List;

public class RechargeListResponse {
    private int total;
    private int pageNumber;
    private int pageSize;
    private List<RechargeModel> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<RechargeModel> getList() {
        return list;
    }

    public void setList(List<RechargeModel> list) {
        this.list = list;
    }
}
