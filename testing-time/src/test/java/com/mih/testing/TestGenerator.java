package com.mih.testing;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;

@Disabled
public class TestGenerator {
    String KAFKA_TEST_TEMPLATE = "package com.mih.testing.kafka;\n" +
            "\n" +
            "import com.mih.testing.PersonService;\n" +
            "import com.mih.testing.cassandra.Person;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.beans.factory.annotation.Value;\n" +
            "import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "import org.springframework.boot.test.mock.mockito.MockBean;\n" +
            "import org.springframework.kafka.test.context.EmbeddedKafka;\n" +
            "\n" +
            "import java.util.concurrent.CountDownLatch;\n" +
            "import java.util.concurrent.TimeUnit;\n" +
            "\n" +
            "import static org.mockito.ArgumentMatchers.any;\n" +
            "import static org.mockito.Mockito.verify;\n" +
            "import static org.mockito.Mockito.when;\n" +
            "\n" +
            "@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,\n" +
            "classes = {KafkaAutoConfiguration.class, KafkaConsumer.class, KafkaProducer.class})\n" +
            "@EmbeddedKafka\n" +
            "class EmbeddedKafka%sTest {\n" +
            "\n" +
            "    @Autowired\n" +
            "    private KafkaProducer producer;\n" +
            "\n" +
            "    @MockBean\n" +
            "    PersonService service;\n" +
            "\n" +
            "    @Value(\"${test.topic}\")\n" +
            "    private String topic;\n" +
            "\n" +
            "    @Test\n" +
            "    public void test() throws InterruptedException {\n" +
            "        CountDownLatch latch = new CountDownLatch(1);\n" +
            "        when(service.save(any())).thenAnswer(r -> {\n" +
            "            latch.countDown();\n" +
            "            return r.getArgument(0);\n" +
            "        });\n" +
            "\n" +
            "        Person person = new Person(\"id-123\", \"test\", 20);\n" +
            "\n" +
            "        producer.send(topic, person);\n" +
            "\n" +
            "        latch.await(10, TimeUnit.SECONDS);\n" +
            "        verify(service).save(any());\n" +
            "    }\n" +
            "}";
    String CASSANDRA_TEST_TEMPLATE = "package com.mih.testing.cassandra;\n" +
            "\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "\n" +
            "import java.util.List;\n" +
            "\n" +
            "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
            "import static org.junit.jupiter.api.Assertions.assertNotNull;\n" +
            "\n" +
            "@SpringBootTest(classes = CassandraConfig.class)\n" +
            "class PersonRepository%sTest {\n" +
            "\n" +
            "    @Autowired\n" +
            "    PersonRepository repository;\n" +
            "\n" +
            "    @Test\n" +
            "    public void testFindAll() {\n" +
            "        List<Person> junit = repository.findAll();\n" +
            "        assertEquals(1, junit.size());\n" +
            "    }\n" +
            "\n" +
            "    @Test\n" +
            "    public void testInsert() {\n" +
            "        Person person = new Person(\"id-1\", \"junit-name\", 20);\n" +
            "        Person saved = repository.save(person);\n" +
            "        assertNotNull(saved);\n" +
            "    }\n" +
            "}";

    @Test
    public void generateTests() {
        generateTests(50, CASSANDRA_TEST_TEMPLATE, "cassandra/PersonRepository");
        generateTests(50, KAFKA_TEST_TEMPLATE, "kafka/EmbeddedKafka");
    }

    private void generateTests(int count, String template, String dir) {
        IntStream.range(0, count)
                .forEach(i -> {
                    String fileName = "src/test/java/com/mih/testing/" + dir +  + i + "Test.java";
                    new File(fileName).delete();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                        writer.append(' ');
                        writer.append(String.format(template, i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}
