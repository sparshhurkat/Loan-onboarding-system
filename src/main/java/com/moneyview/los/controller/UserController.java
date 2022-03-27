package com.moneyview.los.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.UserEntity;
import com.moneyview.los.service.UserService;

@RestController
public class UserController {
	
	@Autowired
    private UserService userService;
	
	//get userid,authtoken,cibilscore,empstatus,address & BAN from user
	//send userid and authtoken to auth service to check for validity
	@PostMapping("/validateUser")
    public ApiResponse<UserEntity> validateUser(@RequestBody UserEntity user){
        return new ApiResponse<>(HttpStatus.OK.value(), "User is authorized",userService.validate(user));
    }
	
}
