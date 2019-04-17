package com.Hi5.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Hi5.dao.JobDao;
import com.Hi5.dao.UserDao;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.Job;
import com.Hi5.model.User;

@RestController
public class JobController 
{
	@Autowired
 private JobDao jobDao;
	@Autowired
 private UserDao userDao;
	
  @RequestMapping(value="/addjob",method=RequestMethod.POST)
public ResponseEntity<?> addJob(@RequestBody Job job,HttpSession session)
{
	  String email=(String)session.getAttribute("loginId");
	  if(email==null)
	  {
		  ErrorClazz errorclazz=new ErrorClazz(5,"You must Login;Login please");
		return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	  }
	  User user=userDao.getUser(email);
	  if(!user.getRole().equals("ADMIN"))
	  {
		 ErrorClazz errorclazz=new ErrorClazz(5,"Access Denied; you are not an an authorised to access");
		return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED);  
	  }
	
	try{
		job.setPostedOn(new Date());
		jobDao.addJob(job);

		 System.out.println("Session Id::::"+session.getId());
		 System.out.println("Session Creation time:::: "+session.getCreationTime());
		 System.out.println("Session Attribute loginId value:::"+session.getAttribute("loginId"));
		}catch(Exception e){
			ErrorClazz errorClazz=new ErrorClazz(1,"Job details not inserted..something went wrong.." +e.getMessage());
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Job>(job,HttpStatus.OK);
}
 @RequestMapping(value="/getalljobs",method=RequestMethod.GET)
  public ResponseEntity<?> getAllJobs(HttpSession session)
  {

		 System.out.println("Session Id::::"+session.getId());
		 System.out.println("Session Creation time:::: "+session.getCreationTime());
		 System.out.println("Session Attribute loginId value:::"+session.getAttribute("loginId"));
		 
		 String email=(String)session.getAttribute("loginId");
		  if(email==null)
		  {
			  ErrorClazz errorclazz=new ErrorClazz(5,"You must Login;Login please");
			return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
		  } 
	  List<Job> jobs=jobDao.getAllJobs();
	  return new ResponseEntity<List<Job>>(jobs,HttpStatus.OK);
  }
}
