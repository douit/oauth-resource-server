package com.rkc.zds.resource.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rkc.zds.resource.dto.UserContactElementDTO;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.UserContactEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ChatParticipant;
import com.rkc.zds.resource.model.Message;
import com.rkc.zds.resource.model.Node;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.UserContactsService;
import com.rkc.zds.resource.service.UserService;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/user/contacts")
public class UserContactsController {

	@Autowired
	UserContactsService userContactsService;

	@Autowired
	UserService userService;

	@Autowired
	ContactService contactService;
	
	@Autowired
	private SimpMessagingTemplate webSocket;
	
	@RequestMapping(value = "/friends/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ChatResponseWrapper> getAllUserFriends(@PathVariable int id,
			HttpServletRequest req) {
				
		List<UserContactEntity> contents = userContactsService.findAllUserContacts(id);
		
		List<ChatResponseWrapper> chatWrapperList = new ArrayList<ChatResponseWrapper>();

		ContactEntity contact;
		for (UserContactEntity userContact : contents) {
			contact = contactService.getContact(userContact.getContactId());
			// ignore contacts that may have been deleted
			if (contact != null) {
				ChatParticipant newChatParticipant = new ChatParticipant();
				//newElement.setId(String.valueOf(element.getContactId()));
				newChatParticipant.setId(userContact.getContactId());
				newChatParticipant.setAvatar("");
				newChatParticipant.setDisplayName(contact.getFirstName()+" "+ contact.getLastName());
				newChatParticipant.setParticipantType(0);
				newChatParticipant.setStatus(0);
				UserEntity user = null;
				if (contact.getUserId() != null) {
					user = userService.getUser(contact.getUserId());
				}
				
				if (user != null) {
					int test = user.getIsLoggedIn();
					if (test == 1) {
						//newElement.setPresenceImageUrl("assets/common/images/small-green-dot.png");
						newChatParticipant.setStatus(0);
					} else {
						//newElement.setPresenceImageUrl("assets/common/images/small-red-dot.png");
						newChatParticipant.setStatus(3);
					}
				} else {
					//newElement.setPresenceImageUrl("assets/common/images/small-red-dot.png");
					newChatParticipant.setStatus(3);
				}

				ChatResponseWrapper wrapper = new ChatResponseWrapper();
				
				wrapper.setParticipant(newChatParticipant);
				
				chatWrapperList.add(wrapper);

			}
		}

		return chatWrapperList;
	}
	
	@RequestMapping(value = "/friends/all/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ChatResponseWrapper> getUserFriends(@PathVariable int id, Pageable pageable,
			HttpServletRequest req) {
		
		Page<UserContactEntity> userContactsPage = userContactsService.findUserContacts(pageable, id);

		List<UserContactEntity> userContactList = userContactsPage.getContent();
		
		List<ChatResponseWrapper> userContactsList = new ArrayList<ChatResponseWrapper>();

		ContactEntity contact;
		for (UserContactEntity userContact : userContactList) {
			contact = contactService.getContact(userContact.getContactId());
			// ignore contacts that may have been deleted
			if (contact != null) {
				ChatParticipant newChatParticipant = new ChatParticipant();
				//newElement.setId(String.valueOf(element.getContactId()));
				newChatParticipant.setId(userContact.getContactId());
				newChatParticipant.setAvatar("");
				newChatParticipant.setDisplayName(contact.getFirstName() + " " + contact.getLastName());
				newChatParticipant.setParticipantType(0);
				newChatParticipant.setStatus(0);
				UserEntity user = null;
				if (contact.getUserId() != null) {
					user = userService.getUser(contact.getUserId());
				}
/*				
				if (user != null) {
					int test = user.getIsLoggedIn();
					if (test == 1) {
						newElement.setPresenceImageUrl("assets/common/images/small-green-dot.png");
					} else {
						newElement.setPresenceImageUrl("assets/common/images/small-red-dot.png");
					}
				} else {
					newElement.setPresenceImageUrl("assets/common/images/small-red-dot.png");
				}
*/
				ChatResponseWrapper wrapper = new ChatResponseWrapper();
				
				wrapper.setParticipant(newChatParticipant);
				
				userContactsList.add(wrapper);

			}
		}

		return userContactsList;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<UserContactElementDTO>> getUserContacts(@PathVariable int id, Pageable pageable,
			HttpServletRequest req) {
		
		Page<UserContactEntity> userContactsPage = userContactsService.findUserContacts(pageable, id);

		List<UserContactEntity> userContactList = userContactsPage.getContent();
		
		List<UserContactElementDTO> userContactDtoList = new ArrayList<UserContactElementDTO>();

		ContactEntity contact;
		for (UserContactEntity userContact : userContactList) {
			contact = contactService.getContact(userContact.getContactId());
			// ignore contacts that may have been deleted
			if (contact != null) {
				UserContactElementDTO newUserContact = new UserContactElementDTO();
				newUserContact.setId(userContact.getId());				
				newUserContact.setUserId(userContact.getUserId());
				newUserContact.setContactId(contact.getId());
				newUserContact.setFirstName(contact.getFirstName());
				newUserContact.setLastName(contact.getLastName());
				newUserContact.setTitle(contact.getTitle());				
				newUserContact.setCompany(contact.getCompany());
				if(contact.getEnabled()==null) {
					newUserContact.setEnabled(1);
				}else {
					newUserContact.setEnabled(contact.getEnabled());					
				}			
				UserEntity user = null;
				if (contact.getUserId() != null) {
					user = userService.getUser(contact.getUserId());
				}
				if (user != null) {
					int test = user.getIsLoggedIn();
					if (test == 1) {
						newUserContact.setPresenceImageUrl("assets/common/images/small-green-dot.png");
					} else {
						newUserContact.setPresenceImageUrl("assets/common/images/small-red-dot.png");
					}
				} else {
					newUserContact.setPresenceImageUrl("assets/common/images/small-red-dot.png");
				}

				userContactDtoList.add(newUserContact);

				// update the user contacts info				
				userContact.setFirstName(contact.getFirstName());
				userContact.setLastName(contact.getLastName());
				userContact.setCompany(contact.getCompany());
				userContact.setTitle(contact.getTitle());
				userContact.setPresenceImageUrl(contact.getPresenceImageUrl());
				userContactsService.saveUserContact(userContact);
				
			} else {
				// delete the user contact, the contact no longer exists
				userContactsService.deleteUserContact(userContact.getId());
			}
		}

		PageRequest pageRequest = PageRequest.of(userContactsPage.getNumber(), userContactsPage.getSize());

		PageImpl<UserContactElementDTO> page = new PageImpl<UserContactElementDTO>(userContactDtoList, pageRequest,
				userContactsPage.getTotalElements());

		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "/filtered/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ContactEntity>> findFilteredContacts(@PathVariable int userId, Pageable pageable,
			HttpServletRequest req) {
		Page<ContactEntity> page = userContactsService.findFilteredContacts(pageable, userId);
		ResponseEntity<Page<ContactEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{userId}/{contactId}", method = RequestMethod.POST)
	public void createUserContact(@PathVariable int userId, @PathVariable int contactId) {
		
		ContactEntity contact = contactService.getContact(contactId);
		
		UserContactEntity userContact = new UserContactEntity();
		userContact.setUserId(userId);
		userContact.setContactId(contactId);
		userContact.setFirstName(contact.getFirstName());		
		userContact.setLastName(contact.getLastName());	
		userContact.setTitle(contact.getTitle());	
		userContact.setCompany(contact.getTitle());		
		userContact.setCompany(contact.getTitle());			
		userContact.setPresenceImageUrl(contact.getPresenceImageUrl());	
		userContact.setEnabled(contact.getEnabled());
		
		userContactsService.saveUserContact(userContact);
		
		UserEntity user = userService.getUser(userId);		
		
		Message<UserEntity> message = new Message();
		Node<UserEntity> node = new Node<UserEntity>(user);
		message.setData(node);
		message.setMessage("User Contacts Changed");

		webSocket.convertAndSend("/topic/user/auth", message);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteUserContact(@PathVariable int id) {
		
		UserContactEntity userContact = userContactsService.findUserContact(id);
		
		userContactsService.deleteUserContact(id);
		
		UserEntity user = userService.getUser(userContact.getUserId());		
		
		Message<UserEntity> message = new Message();
		Node<UserEntity> node = new Node<UserEntity>(user);
		message.setData(node);
		message.setMessage("User Contacts Changed");

		webSocket.convertAndSend("/topic/user/auth", message);		
		
		return Integer.toString(id);
	}
}
