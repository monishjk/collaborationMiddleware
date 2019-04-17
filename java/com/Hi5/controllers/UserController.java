package com.Hi5.controllers;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Hi5.dao.UserDao;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.User;
@RestController
public class UserController
{
@Autowired
 private UserDao userDao;

 @RequestMapping(value="/register",method=RequestMethod.POST)
 public ResponseEntity<?> userRegistration(@RequestBody User user)
 {
	 if(!userDao.isEmailUnique(user.getEmail()))
	 {
		 ErrorClazz errorclazz=new ErrorClazz(0,"Already this Email Id exist,choose another Id for register");
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.INTERNAL_SERVER_ERROR);
	 }

	 if(!userDao.isPhoneNumberUnique(user.getPhonenumber()))
	 {
		 ErrorClazz errorclazz=new ErrorClazz(1,"Entered Number is already exist,enter different phone number");
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.INTERNAL_SERVER_ERROR);
	 }
	 if(user.getRole()=="" || user.getRole()==null)
	 {
		 ErrorClazz errorclazz=new ErrorClazz(2,"Choose the Role ,Role cannot be Empty");
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.INTERNAL_SERVER_ERROR);
	 }
	 try
	 {
		 userDao.userRegistration(user);
	 }
	 catch(Exception e)
	 {
         ErrorClazz errorclazz=new ErrorClazz(3,"Unable to register the details provided"+e.getMessage());
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.INTERNAL_SERVER_ERROR); 
	 }
	 
	 return new ResponseEntity<Void>(HttpStatus.OK);
 }

 @RequestMapping(value="/login",method=RequestMethod.POST)
 public ResponseEntity<?> Login(@RequestBody User user,HttpSession session)
 {
	 User validuser=userDao.login(user);
	 if(validuser==null)
	 {
		 System.out.println("Login id or password is incorrect....");
         ErrorClazz errorclazz=new ErrorClazz(4,"Entered Email/Password is incorrect,Enter valid Email or password");
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	 }
	 else
	 {
		 System.out.println("Login succesfully");
		 System.out.println("Session Attribute:::"+session.getAttribute("loginId"));
		 System.out.println("Session Id::::"+session.getId());
		 System.out.println("Session Creation time:::: "+session.getCreationTime());
		 validuser.setOnline(true);
		 userDao.updateUser(validuser);
		 session.setAttribute("loginId",validuser.getEmail());
		return new ResponseEntity<User>(validuser,HttpStatus.OK); 
	 }
	 
 }
 @RequestMapping(value="/logout",method=RequestMethod.PUT)
 public ResponseEntity<?> logout(HttpSession session)
 {
	String email=(String)session.getAttribute("loginId");
	if(email==null)
	 {
        ErrorClazz errorclazz=new ErrorClazz(5,"You must Login;Login please");
		 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	 }
     	User user=userDao.getUser(email);
	    user.setOnline(false);
	    userDao.updateUser(user);
	    session.removeAttribute("loginId");
	    session.invalidate();
	    return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@RequestMapping(value="/getuser",method=RequestMethod.GET)
	public ResponseEntity<?> getUser(HttpSession session)
	{
		String email=(String)session.getAttribute("loginId");
		if(email==null)
		{

	        ErrorClazz errorclazz=new ErrorClazz(6,"You must Login;Login please");
			 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
		}
	     User user=userDao.getUser(email);
	     return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	@RequestMapping(value="/updateuserdetails",method=RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@RequestBody User user,HttpSession session){
		String email=(String)session.getAttribute("loginId");
	
		 if(email==null){
			 ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
			 return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//401 - login.html
		 }
		 
		 if(!userDao.isUpdatedPhonenumberUnique(user.getPhonenumber(), email)){
				ErrorClazz errorClazz=new ErrorClazz(1,"Phone number already exists.. pls enter another phonenumber");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		 if(user.getRole()=="" || user.getRole()==null){
				ErrorClazz errorClazz=new ErrorClazz(4,"Role cannot be null..");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		 try{
		 userDao.updateUser(user);
		 }catch(Exception e){
			 ErrorClazz errorClazz=new ErrorClazz(6,"Unable to update user profile "+e.getMessage());
			 return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		 return new ResponseEntity<Void>(HttpStatus.OK);
	}
}

