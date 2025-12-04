package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.sale.SaleRequest;
import com.yolge.client.dto.sale.SaleResponse;
import com.yolge.client.dto.sale.SaleDetailRequest;

public class SaleService {

    private static SaleService instance;
    private final RestClient restClient;

    private SaleService() {
        this.restClient = RestClient.getInstance();
    }

    public static synchronized SaleService getInstance() {
        if (instance == null) instance = new SaleService();
        return instance;
    }

    public SaleResponse createSale() {
        SaleRequest req = new SaleRequest();
        return restClient.post("/sales", req, SaleResponse.class);
    }

    public SaleResponse createSale(Long clientId) {
        SaleRequest req = new SaleRequest(clientId);
        return restClient.post("/sales", req, SaleResponse.class);
    }

    public SaleResponse addDetail(Long saleId, Long productId, Integer quantity) {
        SaleDetailRequest req = new SaleDetailRequest(productId, quantity);
        return restClient.post("/sales/" + saleId + "/details", req, SaleResponse.class);
    }

    public SaleResponse removeDetail(Long detailId) {
        return restClient.delete("/sales/details/" + detailId, SaleResponse.class);
    }

    public SaleResponse finalizeSale(Long saleId) {
        return restClient.patch("/sales/" + saleId + "/finalize", SaleResponse.class);
    }

    public SaleResponse cancelSale(Long saleId) {
        return restClient.delete("/sales/" + saleId, SaleResponse.class);
    }
}