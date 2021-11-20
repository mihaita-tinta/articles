package com.mih.spring.magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {
    private static final Logger log = LoggerFactory.getLogger(ProfileRepository.class);

    public Profile getProfile(String id) {
        log.debug("getProfile - id: {}", id);
        return new Profile(id, "profile-" + id);
    }
}
