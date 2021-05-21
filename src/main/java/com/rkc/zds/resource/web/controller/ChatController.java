package com.rkc.zds.resource.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.rkc.zds.resource.service.ContactService;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
public class ChatController {

	final static Logger LOG = LoggerFactory.getLogger(ChatController.class);

	private static final String DEFAULT_PAGE_DISPLAYED_TO_USER = "0";

	private int contactId = 0;

	@Autowired
	ContactService contactService;

	@Value("10")
	private int maxResults;

	@MessageMapping("/chat/create")
	@SendTo("/topic/chat/updated")
	public String save(String post) {
		return post;
	}

	@MessageMapping("/chat/update")
	@SendTo("/topic/chat/updated")
	public String update(String post) {
		return post;
	}
	
	
	@MessageMapping("/chat/delete")
	@SendTo("/topic/chat/deleted")
	public String delete(String post) {
		return post;
	}
}
