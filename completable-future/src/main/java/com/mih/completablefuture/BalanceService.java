package com.mih.completablefuture;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.finagle.http.Status;
import com.twitter.util.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class BalanceService {
    private static final Logger log = LoggerFactory.getLogger(BalanceService.class);
    private final Service<Request, Response> httpClient;
    private final ObjectMapper mapper;

    public BalanceService(Service<Request, Response> httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public CompletableFuture<Balance> getBalance(String accountId) {
        log.debug("getBalance - start");

        Request request = Request.apply(Method.Get(), "/v2/accounts/" + accountId + "/balance");
        request.host("localhost");
        Future<Response> response = httpClient.apply(request);

        return response.toCompletableFuture()
                .thenCompose(r -> {
                    Response res = (Response) r;
                    log.debug("getBalance - received: {}, body: {}", res, res.contentString());
                    if (res.status() != Status.Ok()) {
                        return CompletableFuture.failedFuture(new IllegalStateException("could not get balance"));
                    }
                    try {
                        Balance t = mapper.readValue(res.contentString(), Balance.class);
                        return CompletableFuture.completedFuture(t);
                    } catch (JsonProcessingException e) {
                        log.error("getBalance - error deserializing response", e);
                        return CompletableFuture.failedFuture(e);
                    }
                });
    }
}
