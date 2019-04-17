package com.Hi5.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Hi5.dao.NotificationDao;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.Notification;
@RestController
public class NotificationController 
{
	@Autowired
	private NotificationDao notificationDao;
		@RequestMapping(value="/getnotificationsnotviewed",method=RequestMethod.GET)
		public ResponseEntity<?> getNotificationsNotViewed(HttpSession session)
		{
			String email=(String)session.getAttribute("loginId");
			if(email==null)
			{
				ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
					List<Notification> notificationsNotViewed=notificationDao.getAllNotificationsNotViewed(email);
					return new ResponseEntity<List<Notification>>(notificationsNotViewed,HttpStatus.OK);
		}
		
		@RequestMapping(value="/getnotification/{notificationId}",method=RequestMethod.GET)
		public ResponseEntity<?> getNotification(HttpSession session,@PathVariable int notificationId){
			String email=(String)session.getAttribute("loginId");
			if(email==null)
			{
				ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
			}
			Notification notification=notificationDao.getNotification(notificationId);
			return new ResponseEntity<Notification>(notification,HttpStatus.OK);
		}
		
		@RequestMapping(value="/updatenotification/{notificationId}",method=RequestMethod.PUT)
		public ResponseEntity<?> updateNotificationViewedStatus(HttpSession session,@PathVariable int notificationId){
			String email=(String)session.getAttribute("loginId");
			if(email==null)
			{
				ErrorClazz errorClazz=new ErrorClazz(5,"Please login..");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
			}
			notificationDao.updateNotificactionViewedStatus(notificationId);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}

}
