package com.mongodb.starter;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.starter.models.ApplicationUser;
import com.mongodb.starter.models.Experience;
import com.mongodb.starter.models.PlacementMaterial;
import com.mongodb.starter.repositories.ApplicationUserRepository;
import com.mongodb.starter.repositories.ExperienceRepository;
import com.mongodb.starter.repositories.PlacementMatRepository;

import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private ExperienceRepository experienceRepository;
    @Autowired
    private PlacementMatRepository placementMatRepository;
    @Autowired
    private TestHelper testHelper;
    private String URL;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private MockMvc mvc;
//    @Before
//    public void setup() {
//        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
//    }

    @Autowired
    UserControllerIT(MongoClient mongoClient) {
        createUserCollectionIfNotPresent(mongoClient);
    }

    @PostConstruct
    void setUp() {
        URL = "http://localhost:" + port + "/users";
    }

    @AfterEach
    void tearDown() {
        applicationUserRepository.delete(testHelper.getShivam().getUsername());


    }

//    @BeforeAll
//   void tearDown() {
//       applicationUserRepository.deleteAll();
//    }

    @DisplayName("POST /signup with 1 person")
    @Test
    void postUser() {
        // GIVEN
        // WHEN
        ResponseEntity<ApplicationUser> result = rest.postForEntity(URL + "/signup", testHelper.getShivam(), ApplicationUser.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        experienceRepository.delete(testHelper.getShivam().getUsername());

    }

    @DisplayName("GET /profile")
    @Test
    void getProfile() throws Exception {
        // GIVEN
        postUser();
        //ObjectId idInserted = personInserted.getId();
        ResponseEntity<String> result = rest.postForEntity("http://localhost:" + port +  "/login", testHelper.getShivam(), String.class);
        // WHEN
        //System.out.println(result.getStatusCode());
        //System.out.println(result.getHeaders().get("token"));
        String token=result.getHeaders().get("token").get(0);
       // String basicDigestHeaderValue = "Basic " + new String(Base64.encodeBase64(("ishan:").getBytes()));
       // MvcResult result =  mvc.perform(get(URL + "/profile").
        //ResponseEntity<ApplicationUser> result1 = rest.withBasicAuth(testHelper.getShivam().getUsername(),testHelper.getShivam().getPassword()).getForEntity(URL + "/profile", ApplicationUser.class);
        // THEN
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.put("Authorization",result.getHeaders().get("token"));
        HttpEntity<?> httpEntity=new HttpEntity<>(httpHeaders);
        ResponseEntity<ApplicationUser> result1=rest.exchange(URL + "/profile",HttpMethod.GET,httpEntity,ApplicationUser.class);
        //System.out.println(result1.getBody());
       assertThat(result1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result1.getBody().getUsername()).isEqualTo(testHelper.getShivam().getUsername());
    }

    private void createUserCollectionIfNotPresent(MongoClient mongoClient) {
        // This is required because it is not possible to create a new collection within a multi-documents transaction.
        // Some tests start by inserting 2 documents with a transaction.
        MongoDatabase db = mongoClient.getDatabase("test");
        if (!db.listCollectionNames().into(new ArrayList<>()).contains("users"))
            db.createCollection("users");
    }



}
