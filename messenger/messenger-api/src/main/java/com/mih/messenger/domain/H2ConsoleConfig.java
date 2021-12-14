package com.mih.messenger.domain;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.h2.tools.Server;

@Configuration
public class H2ConsoleConfig {

    private Server webServer;

    private Server tcpServer;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        this.webServer = Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
        this.tcpServer = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.tcpServer.stop();
        this.webServer.stop();
    }

}
