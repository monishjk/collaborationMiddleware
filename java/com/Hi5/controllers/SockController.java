package com.Hi5.controllers;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.Hi5.model.Chat;

@Controller
public class SockController
{
	private List<String> users=new ArrayList<String>();
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	public SockController(SimpMessagingTemplate simpMessagingTemplate){
		this.simpMessagingTemplate=simpMessagingTemplate;
	}
	@SubscribeMapping(value="/join/{username}")
	public List<String> join(@DestinationVariable String username ){
		if(!users.contains(username)){
			users.add(username);
		}
		System.out.println("====JOIN==== " + username);
		simpMessagingTemplate.convertAndSend("/topic/join",username);
		return users;
	}
	@MessageMapping(value="/chat")

	public void chatReveived(Chat chat) {

		if ("all".equals(chat.getTo())) {

			System.out.println("IN Chat Reviever " + chat.getMessage() + " " + chat.getFrom() + " to " + chat.getTo());

			simpMessagingTemplate.convertAndSend("/queue/chats", chat);

		}

		else {

			System.out.println("CHAT TO " + chat.getTo() + " From " + chat.getFrom() + " Message " + chat.getMessage());

			simpMessagingTemplate.convertAndSend("/queue/chats/" + chat.getTo(), chat);

			simpMessagingTemplate.convertAndSend("/queue/chats/" + chat.getFrom(), chat);

		}

	}}
