package com.Hi5.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Hi5.dao.BlogPostDao;
import com.Hi5.dao.NotificationDao;
import com.Hi5.dao.UserDao;
import com.Hi5.model.BlogPost;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.Notification;
import com.Hi5.model.User;
@RestController
public class BlogPostController 
{
	 @Autowired
	private UserDao userDao;
	 @Autowired
    private BlogPostDao blogpostDao;
	 @Autowired
	 private NotificationDao notificationDao;
 
  @RequestMapping(value="/addblogpost",method=RequestMethod.POST)
  public ResponseEntity<?> addBlogPost(HttpSession session,@RequestBody BlogPost blogPost)
  {
	  String email=(String)session.getAttribute("loginId");
	  if(email==null)
	  {
          ErrorClazz errorclazz=new ErrorClazz(1,"Please Login");
			 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	  }
	  blogPost.setPostedOn(new Date());
	  User author=userDao.getUser(email);
	  blogPost.setAuthor(author);
	  try
	  {
		  if(author.getRole().equals("ADMIN"))
			 blogPost.setApproved(true);
		  blogpostDao.addBlogPost(blogPost);
	  }
	  catch(Exception e)
	  {
          ErrorClazz errorclazz=new ErrorClazz(2,"Unable to insert blogpost"+e.getMessage());
		   return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.INTERNAL_SERVER_ERROR); 
	  }
	  return new ResponseEntity<Void>(HttpStatus.OK);
  }
  @RequestMapping(value="/blogsapproved",method=RequestMethod.GET)
  public ResponseEntity<?> getBlogsApproved(HttpSession session)
  {
	  String email=(String)session.getAttribute("loginId");
	  if(email==null)
	  {
          ErrorClazz errorclazz=new ErrorClazz(3,"Please Login");
			 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	  } 
	  List<BlogPost> blogsApproved=blogpostDao.getBlogsApproved();
	  return new ResponseEntity<List<BlogPost>>(blogsApproved,HttpStatus.OK);
  }

  @RequestMapping(value="/blogswaitingforapproval",method=RequestMethod.GET)
  public ResponseEntity<?> getBlogsWaitingForApproval(HttpSession session)
  {
	  String email=(String)session.getAttribute("loginId");
	  if(email==null)
	  {
          ErrorClazz errorclazz=new ErrorClazz(3,"Please Login");
		  return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	  } 
	  User user=userDao.getUser(email);
	  if(!user.getRole().equals("ADMIN"))
	  {
         ErrorClazz errorclazz=new ErrorClazz(3,"Please Login");
	     return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
	  }
	  List<BlogPost> blogsWaitingForApproval=blogpostDao.getBlogsWaitingForApproval();
	  return new ResponseEntity<List<BlogPost>>(blogsWaitingForApproval,HttpStatus.OK);
  }
  @RequestMapping(value="/getblog/{blogPostId}")
  public ResponseEntity<?> getBlog(HttpSession session,@PathVariable int blogPostId){
  	
  	String email=(String)session.getAttribute("loginId");
  	if(email==null){
  		ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
  		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
  	}
  	BlogPost blogPost=blogpostDao.getBlog(blogPostId);
  	System.out.println("blogposst id is:::"+blogPostId);
  	return new ResponseEntity<BlogPost>(blogPost,HttpStatus.OK);
  }


@RequestMapping(value="/approveblogpost",method=RequestMethod.PUT)
public ResponseEntity<?> approveBlogPost(HttpSession session,@RequestBody BlogPost blogPost){
	
		String email=(String)session.getAttribute("loginId");
		if(email==null){
			ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		
		User user=userDao.getUser(email);
		if(!user.getRole().equals("ADMIN")){
			ErrorClazz errorClazz=
				new ErrorClazz(7,"Access Denied.. You are not authorized to view the blogs waiting for approval");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		   blogPost.setApproved(true);
		   blogpostDao.approveBlogPost(blogPost);
		 Notification notification=new Notification();
	  	    notification.setApprovedOrRejected("Approved");
	  	    notification.setBlogTitle(blogPost.getBlogTitle());
	      	notification.setUserToBeNotified(blogPost.getAuthor());
	  		notificationDao.addNotification(notification);
	  	 
		return new ResponseEntity<Void>(HttpStatus.OK);
}

  @RequestMapping(value="/rejectblogpost/{rejectionReason}",method=RequestMethod.PUT)
  public ResponseEntity<?> rejectBlogPost(HttpSession session,@PathVariable String rejectionReason,@RequestBody BlogPost blogPost){
  	
  		String email=(String)session.getAttribute("loginId");
  		if(email==null){
  			ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
  			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
  		}
  		
  		User user=userDao.getUser(email);
  		if(!user.getRole().equals("ADMIN"))
  		{
  			ErrorClazz errorClazz=
  				new ErrorClazz(7,"Access Denied.. You are not authorized to view the blogs waiting for approval");
  			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
  		}
  		Notification notification=new Notification();
  	    notification.setApprovedOrRejected("Rejected");
  	    notification.setBlogTitle(blogPost.getBlogTitle());
      	notification.setUserToBeNotified(blogPost.getAuthor());
      	notification.setRejectionReason(rejectionReason);
  		notificationDao.addNotification(notification);
  		blogpostDao.rejectBlogPost(blogPost);
  		return new ResponseEntity<Void>(HttpStatus.OK);
  }
}