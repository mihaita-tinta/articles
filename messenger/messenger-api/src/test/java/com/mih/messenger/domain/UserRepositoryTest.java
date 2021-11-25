package com.mih.messenger.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test
    public void test() {
        User user = new User();
        user.setUsername("junit");
        repository.save(user);
    }

    @Test
    public void testValidation() {
        User user = new User();

        ConstraintViolationException notNull = Assertions.assertThrows(ConstraintViolationException.class,
                () -> repository.saveAndFlush(user));

        assertEquals("Validation failed for classes [com.mih.messenger.domain.User] during persist time for groups [javax.validation.groups.Default, ]\n" +
                "List of constraint violations:[\n" +
                "\tConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=username, rootBeanClass=class com.mih.messenger.domain.User, messageTemplate='{javax.validation.constraints.NotNull.message}'}\n" +
                "]", notNull.getMessage());
    }

    @Test
    public void testValidationUnique() {
        User user2 = new User();
        user2.setUsername("junit-user");
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(user2));
    }

    @Test
    public void testFind() {
        assertTrue(repository.findByUsername("junit-user").isPresent());
    }


}
