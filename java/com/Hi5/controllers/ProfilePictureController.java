package com.Hi5.controllers;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.Hi5.dao.ProfilePictureDao;
import com.Hi5.model.ErrorClazz;
import com.Hi5.model.ProfilePicture;

@RestController
public class ProfilePictureController
{
	@Autowired
	private ProfilePictureDao profilePictureDao;

	@RequestMapping(value="/uploadprofilepic",method=RequestMethod.POST)
	public ResponseEntity<?> uploadProfilePicture(@RequestParam MultipartFile image, HttpSession session) 
	{
		System.out.println("Entering to the upload picutre");
		String email=(String)session.getAttribute("loginId");
		if(email==null)
		 {
	        ErrorClazz errorclazz=new ErrorClazz(5,"You must Login;Login please");
			 return new ResponseEntity<ErrorClazz>(errorclazz,HttpStatus.UNAUTHORIZED); 
		 }
		ProfilePicture profilePicture=new ProfilePicture();
		profilePicture.setEmail(email);
		try {
			profilePicture.setImage(image.getBytes());
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		profilePictureDao.saveOrUpdateProfilePicture(profilePicture);
		return new ResponseEntity<ProfilePicture>(profilePicture,HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/getimage",method=RequestMethod.GET)
	public @ResponseBody byte[] getImage(@RequestParam String email, HttpSession session)
	{
		System.out.println(email);
		String authEmail=(String)session.getAttribute("loginId");
		if(authEmail==null) 
		{
			return null;
		}
		ProfilePicture profilePicture=profilePictureDao.getProfilePicture(email);
		if(profilePicture==null) 
			return null;
		else
			return profilePicture.getImage();
	}

}
