package com.mongodb.starter.controllers;

import com.mongodb.starter.models.ApplicationUser;
import com.mongodb.starter.models.Experience;
import com.mongodb.starter.models.PlacementMaterial;
import com.mongodb.starter.repositories.ApplicationUserRepository;
import com.mongodb.starter.repositories.ExperienceRepository;
import com.mongodb.starter.repositories.PlacementMatRepository;
import com.mongodb.starter.services.ApplicationUserDetailsService;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mongodb.starter.payload.response.MessageResponse;
import io.jsonwebtoken.impl.DefaultClaims;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ApplicationUserRepository applicationUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ApplicationUserDetailsService userDetailsService;
    private final ExperienceRepository experienceRepository;
    private final PlacementMatRepository placementMatRepository;

    public UserController(ApplicationUserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,ApplicationUserDetailsService userDetailsService,
                          ExperienceRepository experienceRepository,PlacementMatRepository placementMatRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDetailsService = userDetailsService;
        this.experienceRepository = experienceRepository;
        this.placementMatRepository = placementMatRepository;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody ApplicationUser user) {
        logger.info("Entered /users/signup post api");
        boolean temp = applicationUserRepository.existsByUsername(user.getUsername());
        if(temp){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username already exists!"));
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);
        Experience exp = new Experience();
        exp.setExp("");
        exp.setImagePath("");
        exp.setUsername(user.getUsername());
        experienceRepository.save(exp);
        logger.info("Exited /users/signup post api");
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }
    @GetMapping("/profile")
    public ResponseEntity<ApplicationUser> getProfile(Authentication authentication) {
            logger.info("Entered /users/profile get api");
            DefaultClaims ans = (DefaultClaims) authentication.getPrincipal();
            String name = ans.getSubject();
            ApplicationUser user = applicationUserRepository.findOne(name);
            logger.info("Exited /users/profile get api");
            return ResponseEntity.ok(user);

    }

    @PutMapping("/profile")
    public ApplicationUser putProfile(@RequestBody ApplicationUser user){
        logger.info("Entered /users/profile put api");
        logger.info("Exited /users/profile put api");
        return applicationUserRepository.update(user);
    }

    @GetMapping("/experience")
    public ResponseEntity<Experience> getExperience(Authentication authentication) {
        logger.info("Entered /users/experience get api");
            DefaultClaims ans = (DefaultClaims) authentication.getPrincipal();
            String name = ans.getSubject();
            Experience exp = experienceRepository.findOne(name);
        logger.info("Exited /users/experience get api");
            return ResponseEntity.ok(exp);


    }

    @PutMapping("/experience")
    public Experience putExp(@RequestBody Experience experience){
        logger.info("Entered /users/experience put api");
        return experienceRepository.update(experience);
    }

    @GetMapping("/experiences")
    public List<Experience> getAllExp(Authentication authentications) {
        logger.info("Entered /users/experiences get api");
        return experienceRepository.findAll();
    }

    @GetMapping("/placementMaterial/{sub}")
    public List<PlacementMaterial> getMaterial(@PathVariable String sub ) {
        logger.info("Entered /users/placementMaterial/{sub} get api");
            return placementMatRepository.findAllBySubject(sub);

    }
    @PostMapping("/placementMaterial")
    public PlacementMaterial postMaterial(@RequestBody PlacementMaterial mat){
        logger.info("Entered /users/placementMaterial/ post api");
        return placementMatRepository.save(mat);
    }
    @DeleteMapping("/deleteUsers")
    public Long deletePersons() {
        return applicationUserRepository.deleteAll();
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        logger.error("Internal server error.");
        return e;
    }
}
