package com.Hi5.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Hi5.dao.FriendDao;
import com.Hi5.dao.UserDao;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.Friend;
import com.Hi5.model.User;

@RestController
public class FriendController
{
	@Autowired
	private FriendDao friendDao;
	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value="/suggestedusers",method=RequestMethod.GET)
	public ResponseEntity<?> getAllSuggestedUsers(HttpSession session){
		String email=(String)session.getAttribute("loginId");
		if(email==null) 
		{
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		List<User> suggestedUsers=friendDao.getAllSuggestedUsers(email);
		return new ResponseEntity<List<User>>(suggestedUsers,HttpStatus.OK);
	}
	
	@RequestMapping(value="/addfriend",method=RequestMethod.POST)
	public ResponseEntity<?> addFriend(@RequestBody User toId, HttpSession session){
		String email=(String)session.getAttribute("loginId");
		if(email==null) {
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		User fromId=userDao.getUser(email);
		Friend friend=new Friend();
		friend.setFromId(fromId);
		friend.setToId(toId);
		friend.setStatus('P');
		friendDao.addFriend(friend);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/pendingrequests",method=RequestMethod.GET)
	public ResponseEntity<?> pendingRequests(HttpSession session){
		String email=(String)session.getAttribute("loginId");
		if(email==null) {
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		List<Friend> pendingRequests=friendDao.getPendingRequests(email);
		System.out.println("pending request "+pendingRequests);
		return new ResponseEntity<List<Friend>>(pendingRequests,HttpStatus.OK);
	}
	
	@RequestMapping(value="/acceptrequest",method=RequestMethod.PUT)
	public ResponseEntity<?> acceptFriendRequest(@RequestBody Friend pendingRequest, HttpSession session){
	
		String email=(String)session.getAttribute("loginId");
		if(email==null)
		{
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		pendingRequest.setStatus('A');
		friendDao.acceptRequest(pendingRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/deletefriendrequest",method=RequestMethod.PUT)
	public ResponseEntity<?> deleteFriendRequest(@RequestBody Friend pendingRequest,HttpSession session){
		
		String email=(String)session.getAttribute("loginId");
		if(email==null) {
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		friendDao.deleteRequest(pendingRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/listoffriends",method=RequestMethod.GET)
	public ResponseEntity<?> listOfFriends(HttpSession session){
		String email=(String)session.getAttribute("loginId");
		if(email==null) {
			ErrorClazz errorClazz=new ErrorClazz(4,"Uauthorized access.. please login.....");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		List<User> friends=friendDao.listOfFriends(email);
		return new ResponseEntity<List<User>>(friends,HttpStatus.OK);
	}
}
