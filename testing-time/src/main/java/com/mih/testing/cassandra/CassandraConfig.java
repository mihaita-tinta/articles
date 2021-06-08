package com.mih.testing.cassandra;

import com.datastax.driver.core.Session;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint;
import info.archinnov.achilles.embedded.CassandraEmbeddedServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.io.File;
import java.net.InetSocketAddress;

@Configuration
@EnableCassandraRepositories
@EnableAutoConfiguration
public class CassandraConfig {
    @Value("${random.int[9000,9999]}")
    int cqlPort;

    @Bean
    @DependsOn("cassandra")
    public CqlSession session() {
        return CqlSession.builder()
                .withKeyspace("mykeyspace")
//                .withLocalDatacenter("datacenter1")
//                .addContactEndPoint(new DefaultEndPoint(InetSocketAddress.createUnresolved("127.0.0.1", cqlPort)))
                .build();
    }

    @Bean
    public Session cassandra(@Value("target/cassandra_embedded/data-${random.int}") String dataFolder) {
        new File(dataFolder).mkdir();

        return CassandraEmbeddedServerBuilder
                .builder()
                .withClusterName("Test Cluster")
                .withListenAddress("127.0.0.1")
                .withRpcAddress("127.0.0.1")
                .withBroadcastAddress("127.0.0.1")
                .withBroadcastRpcAddress("127.0.0.1")
//                .withCQLPort(cqlPort)
                .withKeyspaceName("mykeyspace")
                .withScript("data.cql")
                .withDataFolder(dataFolder)
                .cleanDataFilesAtStartup(true)
                .buildNativeSession();
    }
}
