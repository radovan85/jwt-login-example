package com.radovan.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.radovan.spring.dto.AuthenticationResponse;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.UserService;
import com.radovan.spring.utils.JwtUtil;

@RestController
public class MainController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private JwtUtil jwtTokenUtil;
	
	@GetMapping(value = "/")
	public ResponseEntity<String> welcome(){
		return ResponseEntity.ok().body("Welcome authorized user");
	}
	
	@PostMapping(value = "/register")
	public ResponseEntity<String> registerUser(@Validated @RequestBody UserDto user,Errors errors){
		
		if(errors.hasErrors()) {
			Error error = new Error("Data is not validated!");
			throw new DataNotValidatedException(error);
		}
		
		UserDto storedUser = userService.storeUser(user);
		return ResponseEntity.ok().body("User stored to database with email " + storedUser.getEmail());
	}
	
	@PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToke(@RequestBody com.radovan.spring.dto.AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User or Password incorrect", e);
        }
        
        
        final UserDetails userDetails = userService.getUserByEmail(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
	
	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value="/admin")
	public ResponseEntity<String> adminPanel(){
		return ResponseEntity.ok().body("Welcome to Admin Panel");
	}
	
	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value="/user")
	public ResponseEntity<String> userPanel(){
		return ResponseEntity.ok().body("Welcome to User Panel");
	}
	
	

	
}
