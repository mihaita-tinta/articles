package com.mih.completablefuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class AccountServiceTest {

    @Autowired
    AccountService service;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        assertNotNull(service.getAccounts()
                .get());

    }
    @Test
    public void testWebFlux() {
        assertNotNull(service.getAccountsWebFlux().blockLast());

    }

}
