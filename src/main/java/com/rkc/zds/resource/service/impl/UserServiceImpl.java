package com.rkc.zds.resource.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.dto.LoginDTO;
import com.rkc.zds.resource.entity.AuthorityEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.exception.UserAlreadyExistException;
import com.rkc.zds.resource.repository.AuthorityRepository;
import com.rkc.zds.resource.repository.ContactRepository;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ContactService contactService;

	@Override
	public UserEntity findByUserName(String userName) {
		Optional<UserEntity> userDto = userRepository.findByUserName(userName);

		UserEntity user = null;

		if (userDto.isPresent()) {
			user = userDto.get();
		}

		return user;
	}

	@Override
	public Page<UserEntity> findUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public UserEntity findById(Integer id) {
		return userRepository.getOne(id);
	}

	@Override
	public List<UserEntity> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public UserEntity getUser(int id) {

		Optional<UserEntity> user = userRepository.findById(id);
		if (user.isPresent())
			return user.get();
		else
			return null;
	}

	@Override
	public void updateUser(UserEntity user) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userRepository.saveAndFlush(user);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void saveUser(UserEntity user) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userRepository.save(user);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteUser(int id) {

		UserEntity user = null;

		Optional<UserEntity> userOptional = null;

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userOptional = userRepository.findById(id);

			if (userOptional.isPresent()) {
				user = userOptional.get();
			}

			if (user != null) {
				List<AuthorityEntity> userAuthorities = user.getAuthorities();

				// delete authorities for this user
				for (AuthorityEntity authority : userAuthorities) {
					authorityRepository.deleteById(authority.getId());
				}
				
				// delete resumes for this user
				
				// delete 
				//			articles, 
				//			article comments,
				//			article favorites,
				//			article follows,
				
			}
			
			
				
			userRepository.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public UserEntity registerNewUserAccount(final UserEntity accountDto) {
		if (loginExist(accountDto.getLogin())) {
			throw new UserAlreadyExistException("There is an account with that userName: " + accountDto.getLogin());
		}

		String keycloakPassword = accountDto.getPassword();

		accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
		accountDto.setEnabled(1);
		accountDto.setIsLoggedIn(0);
		UserEntity user = userRepository.save(accountDto);

		AuthorityEntity role = new AuthorityEntity();
		role.setUserName(accountDto.getLogin());
		role.setAuthority("ROLE_USER");

		authorityRepository.save(role);

		linkUsertoContact(user);

		Keycloak kc = Keycloak.getInstance("https://www.zdslogic.com/keycloak/auth", "zdslogic", "richard.campion",
				"ArcyAdmin8246+", "admin-cli");

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(keycloakPassword);

		UserRepresentation keycloakUser = new UserRepresentation();
		keycloakUser.setUsername(accountDto.getLogin());
		keycloakUser.setFirstName(accountDto.getFirstName());
		keycloakUser.setLastName(accountDto.getLastName());
		keycloakUser.setEmail(accountDto.getEmail());
		keycloakUser.setCredentials(Arrays.asList(credential));
		keycloakUser.setEnabled(true);
		keycloakUser.setRealmRoles(Arrays.asList("user"));

		// Get realm
		RealmResource realmResource = kc.realm("zdslogic");
		UsersResource usersRessource = realmResource.users();

		// Create Keycloak user
		Response result = null;
		try {
			result = usersRessource.create(keycloakUser);
		} catch (Exception e) {
			System.out.println(e);
		}

		if (result == null || result.getStatus() != 201) {
			System.err.println("Couldn't create Keycloak user.");
			UserEntity temp = userRepository.findByLogin(accountDto.getLogin());
			userRepository.delete(temp);
			authorityRepository.delete(role);

		} else {
			System.out.println("Keycloak user created.... verify in keycloak!");
		}

		return user;

	}

	private void linkUsertoContact(UserEntity user) {
		List<ContactEntity> contacts = contactService.searchContactsByLastNameAndFirstName(user.getLastName(),
				user.getFirstName());

		if (contacts.size() == 1) {
			ContactEntity contact = contacts.get(0);

			user.setContactId(contact.getId());

			userRepository.save(user);

			contact.setUserId(user.getId());

			contactRepository.save(contact);

		} else if (contacts.size() == 0) {

			ContactEntity contact = new ContactEntity();
			contact.setFullName(user.getFirstName() + " " + user.getLastName());
			contact.setFirstName(user.getFirstName());
			contact.setLastName(user.getLastName());
			contact.setCompany("Unknown");
			contact.setTitle("Unknown");
			contact.setUserId(user.getId());
			contact.setEnabled(1);

			contact = contactRepository.save(contact);

			user.setContactId(contact.getId());

			userRepository.save(user);

		}
	}

	private boolean loginExist(final String login) {

		UserEntity user = userRepository.findByLogin(login);
		if (user != null) {

			return true;
		}

		return false;
	}

	@Override
	public Page<UserEntity> searchUsers(Pageable pageable, Specification<UserEntity> spec) {
		return userRepository.findAll(spec, pageable);
	}

	public UserEntity changePassword(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
		Optional<UserEntity> user = userRepository.findByUserName(loginDTO.getLogin());

		UserEntity userDto = null;

		if (user.isPresent()) {
			userDto = user.get();

			userDto.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
			userDto.setEnabled(1);

			userDto = userRepository.save(userDto);
		}
		return userDto;
	}

	@Override
	public Page<AuthorityEntity> findAuthorities(Pageable pageable, String userName) {

		Page<AuthorityEntity> authority = authorityRepository.findByUserName(pageable, userName);

		return authority;
	}

	@Override
	public AuthorityEntity getAuthority(int id) {
		Optional<AuthorityEntity> authority = authorityRepository.findById(id);
		if (authority.isPresent())
			return authority.get();
		else
			return null;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAuthority(AuthorityEntity authority) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			authorityRepository.saveAndFlush(authority);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void saveAuthority(AuthorityEntity role) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			authorityRepository.save(role);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteAuthority(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			authorityRepository.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
