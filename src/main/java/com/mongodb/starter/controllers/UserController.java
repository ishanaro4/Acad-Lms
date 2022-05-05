package com.mongodb.starter.controllers;

import com.mongodb.starter.models.ApplicationUser;
import com.mongodb.starter.models.Experience;
import com.mongodb.starter.models.PlacementMaterial;
import com.mongodb.starter.repositories.ApplicationUserRepository;
import com.mongodb.starter.repositories.ExperienceRepository;
import com.mongodb.starter.repositories.PlacementMatRepository;
import com.mongodb.starter.services.ApplicationUserDetailsService;
import io.jsonwebtoken.Claims;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
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

    /*@PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody ApplicationUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));

    }*/

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody ApplicationUser user) {
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
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @GetMapping("/profile")
    public ResponseEntity<ApplicationUser> getProfile(Authentication authentication) {
        if(authentication.isAuthenticated()){
            DefaultClaims ans = (DefaultClaims) authentication.getPrincipal();
            String name = ans.getSubject();
            ApplicationUser user = applicationUserRepository.findOne(name);
            return ResponseEntity.ok(user);
        }
        else{
            return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
        }

    }

    @PutMapping("/profile")
    public ApplicationUser putProfile(@RequestBody ApplicationUser user){
        return applicationUserRepository.update(user);
    }

    @GetMapping("/experience")
    public ResponseEntity<Experience> getExperience(Authentication authentication) {
        if(authentication.isAuthenticated()){
            DefaultClaims ans = (DefaultClaims) authentication.getPrincipal();
            String name = ans.getSubject();
            Experience exp = experienceRepository.findOne(name);
            return ResponseEntity.ok(exp);
        }
        else{
            return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
        }

    }

    @PutMapping("/experience")
    public Experience putExp(@RequestBody Experience experience){
        return experienceRepository.update(experience);
    }

    @GetMapping("/placementMaterial/{sub}")
    public List<PlacementMaterial> getMaterial(@PathVariable String sub ) {
            return placementMatRepository.findAllBySubject(sub);

    }
    @PostMapping("/placementMaterial")
    public PlacementMaterial postMaterial(@RequestBody PlacementMaterial mat){
        return placementMatRepository.save(mat);
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
