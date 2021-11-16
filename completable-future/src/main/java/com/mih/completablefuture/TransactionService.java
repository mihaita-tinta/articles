package com.mih.completablefuture;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final Service<Request, Response> httpClient;
    private final ObjectMapper mapper;
    private final WebClient webClient;

    public TransactionService(Service<Request, Response> httpClient, ObjectMapper mapper, WebClient webClient) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.webClient = webClient;
    }

    public CompletableFuture<List<Transaction>> getTransactions(String accountId) {
        log.debug("getTransactions - start");

        Request request = Request.apply(Method.Get(), "/v2/accounts/" + accountId +"/transactions");
        request.host("localhost");
        Future<Response> response = httpClient.apply(request);

        return response.toCompletableFuture()
                .thenCompose(r -> {
                    Response res = (Response) r;
                    log.debug("getTransactions - received: {}, body: {}", res, res.contentString());
                    try {
                        List<Transaction> t = mapper.readValue(res.contentString(), new TypeReference<>() {});
                        return CompletableFuture.completedFuture(t);
                    } catch (JsonProcessingException e) {
                        log.error("getTransactions - error deserializing response", e);
                        return CompletableFuture.failedFuture(e);
                    }
                });
    }


    public Flux<Transaction> getTransactionsWebFlux(String accountId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/accounts/{accountId}/transactions")
                        .build(accountId))
                .retrieve()
                .bodyToFlux(Transaction.class);
    }
}
