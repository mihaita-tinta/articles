package com.mih.testing;

import com.mih.testing.cassandra.Person;
import com.mih.testing.cassandra.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks
    PersonService personService;

    @Mock
    PersonRepository repository;
    @Mock
    Person person;

    @Test
    public void test() {
        when(repository.save(any())).thenReturn(person);

        personService.save(person);

        verify(repository).save(any());
    }

}
