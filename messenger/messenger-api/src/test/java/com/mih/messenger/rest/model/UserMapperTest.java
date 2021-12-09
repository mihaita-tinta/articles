package com.mih.messenger.rest.model;

import com.mih.messenger.domain.User;
import io.github.webauthn.domain.WebAuthnUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {


    @Test
    public void test() {
        WebAuthnUser user = new WebAuthnUser();
        user.setUsername("abc");
        user.setId(1L);
        User u = UserMapper.INSTANCE.fromWebAuthn(user);

        assertEquals(user.getId(), u.getId());
        assertEquals(user.getUsername(), u.getUsername());
        assertNotNull(u.getDateCreated());
    }
}
