package com.mongodb.starter;

import com.mongodb.starter.models.ApplicationUser;
import com.mongodb.starter.models.Experience;
import com.mongodb.starter.models.PlacementMaterial;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
class TestHelper {

    ApplicationUser getIshan() {
        return new ApplicationUser().setUsername("ishan").setPassword("").setDescription("hello").setMobile(1234);
    }

    ApplicationUser getShivang() {
        return new ApplicationUser().setUsername("shivang").setPassword("").setDescription("hello").setMobile(1234);
    }

    ApplicationUser getShivam() {
        return new ApplicationUser().setUsername("shivam").setPassword("").setDescription("hello").setMobile(1234);
    }

    List<ApplicationUser> getListIshanShivang() {
        return asList(getIshan(), getShivang());
    }
}
