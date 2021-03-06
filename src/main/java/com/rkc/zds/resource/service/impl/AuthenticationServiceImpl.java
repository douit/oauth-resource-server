package com.rkc.zds.resource.service.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.PlainJWT;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.Message;
import com.rkc.zds.resource.model.Node;
import com.rkc.zds.resource.service.UserService;

@Service
public class AuthenticationServiceImpl {

	public static final String CSRF_CLAIM_HEADER = "X-HMAC-CSRF";
	public static final String ACCESS_TOKEN_COOKIE = "access_token";
	public static final String JWT_CLAIM_LOGIN = "login";

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private SimpMessagingTemplate webSocket;

	/**
	 * Authenticate a user in Spring Security The following headers are set in the
	 * response: - X-TokenAccess: JWT - X-Secret: Generated secret in base64 using
	 * SHA-256 algorithm - WWW-Authenticate: Used algorithm to encode secret The
	 * authenticated user is set in the Spring Security context The generated secret
	 * is stored in a static list for every user
	 * 
	 * @param loginDTO credentials
	 * @param response http response
	 * @return UserDTO instance
	 * @throws InterruptedException
	 * @throws HmacException        / public UserDto authenticate(LoginDto loginDTO,
	 *                              HttpServletRequest request, HttpServletResponse
	 *                              response) throws HmacException {
	 *                              UsernamePasswordAuthenticationToken
	 *                              authenticationToken = new
	 *                              UsernamePasswordAuthenticationToken(
	 *                              loginDTO.getLogin(), loginDTO.getPassword());
	 *                              Authentication authentication = null;
	 * 
	 *                              try { authentication =
	 *                              authenticationManager.authenticate(authenticationToken);
	 *                              } catch (BadCredentialsException e) { try {
	 *                              response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	 *                              "Unauthorized"); return null; } catch
	 *                              (IOException e1) { // TODO Auto-generated catch
	 *                              block e1.printStackTrace(); } } catch
	 *                              (AuthenticationCredentialsNotFoundException x) {
	 *                              try {
	 *                              response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	 *                              "Unauthorized"); return null; } catch
	 *                              (IOException e2) { // TODO Auto-generated catch
	 *                              block e2.printStackTrace(); } }
	 * 
	 *                              SecurityContextHolder.getContext().setAuthentication(authentication);
	 * 
	 *                              // Retrieve security user after authentication
	 *                              UserDetails userDetails =
	 *                              userDetailsService.loadUserByUsername(loginDTO.getLogin());
	 * 
	 *                              UserDto userDTO =
	 *                              userService.findByUserName(userDetails.getUsername());
	 * 
	 *                              // SecurityUser securityUser = (SecurityUser) //
	 *                              userDetailsService.loadUserByUsername(loginDTO.getLogin());
	 *                              SecurityUser securityUser = new
	 *                              SecurityUser(userDTO.getId(),
	 *                              userDetails.getUsername(),
	 *                              userDetails.getPassword(), null,
	 *                              userDetails.getAuthorities());
	 * 
	 *                              // Parse Granted authorities to a list of string
	 *                              authorities List<String> authorities = new
	 *                              ArrayList<>(); for (GrantedAuthority authority :
	 *                              securityUser.getAuthorities()) {
	 *                              authorities.add(authority.getAuthority()); }
	 * 
	 *                              // Get Hmac signed token String csrfId =
	 *                              UUID.randomUUID().toString(); Map<String,
	 *                              String> customClaims = new HashMap<>();
	 *                              customClaims.put(HmacSigner.ENCODING_CLAIM_PROPERTY,
	 *                              HmacUtils.HMAC_SHA_256);
	 *                              customClaims.put(JWT_CLAIM_LOGIN,
	 *                              loginDTO.getLogin());
	 *                              customClaims.put(CSRF_CLAIM_HEADER, csrfId);
	 * 
	 *                              // Generate a random secret // String
	 *                              privateSecret = HmacSigner.generateSecret(); //
	 *                              String publicSecret =
	 *                              HmacSigner.generateSecret();
	 * 
	 *                              //Get jwt secret from properties String
	 *                              jwtSecret =
	 *                              securityProperties.getJwt().getSecret();
	 * 
	 *                              //Get hmac secret from config String
	 *                              hmacSharedSecret =
	 *                              securityProperties.getHmac().getSecret();
	 * 
	 *                              // Jwt is generated using the secret defined in
	 *                              configuration file HmacToken hmacToken =
	 *                              SecurityUtils.getSignedToken(jwtSecret,loginDTO.getLogin(),
	 *                              SecurityService.JWT_TTL,customClaims);
	 * 
	 *                              for (UserDto userDto : userService.getUsers()) {
	 *                              if (userDto.getId() == (securityUser.getId())) {
	 *                              // Store in cache both private and public
	 *                              secrets userDto.setPublicSecret(jwtSecret);
	 *                              userDto.setPrivateSecret(hmacSharedSecret); } }
	 * 
	 *                              // Add jwt as a cookie Cookie jwtCookie = new
	 *                              Cookie(ACCESS_TOKEN_COOKIE, hmacToken.getJwt());
	 *                              //jwtCookie.setPath(request.getContextPath().length()
	 *                              > 0 ? request.getContextPath() : "/");
	 *                              System.out.println("request.getContextPath:"+request.getContextPath());
	 *                              jwtCookie.setPath("/");
	 *                              jwtCookie.setMaxAge(securityProperties.getJwt().getMaxAge());
	 *                              //Cookie cannot be accessed via JavaScript
	 *                              jwtCookie.setHttpOnly(true);
	 * 
	 *                              // Set public secret and encoding in headers
	 *                              response.setHeader(HmacUtils.X_SECRET,
	 *                              hmacSharedSecret);
	 *                              response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
	 *                              HmacUtils.HMAC_SHA_256);
	 *                              response.setHeader(CSRF_CLAIM_HEADER, csrfId);
	 * 
	 *                              // Set JWT as a cookie
	 *                              response.addCookie(jwtCookie);
	 * 
	 *                              // UserDto userDTO = new UserDto();
	 *                              userDTO.setId(securityUser.getId());
	 *                              userDTO.setLogin(securityUser.getUsername()); //
	 *                              userDTO.setAuthorities(securityUser.getAuthorities());
	 *                              userDTO.setProfile(securityUser.getProfile());
	 *                              return userDTO; }
	 */
	public UserEntity authenticateViaSSO(HttpServletRequest request, HttpServletResponse response)
			throws InterruptedException {

		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		KeycloakPrincipal<?> principal = (KeycloakPrincipal) token.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
		AccessToken accessToken = session.getToken();

		Collection<GrantedAuthority> roles = token.getAuthorities();

		System.out.println("User:" + accessToken.getPreferredUsername());
		for (String role : token.getAccount().getRoles()) {
			System.out.println("===========+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Role : " + role);
		}

		String userName = accessToken.getPreferredUsername();
		UserEntity userDTO = userService.findByUserName(userName);
		userDTO.setIsLoggedIn(1);
		userService.saveUser(userDTO);

		// send a message to subscribed apps that a user has logged in
		// TimeUnit.SECONDS.sleep(2);
		Message<UserEntity> message = new Message();
		Node<UserEntity> node = new Node<UserEntity>(userDTO);
		message.setData(node);
		message.setMessage("Logged In");
		webSocket.convertAndSend("/topic/user/auth", message);

		return userDTO;
	}

	public UserEntity getUser(HttpServletRequest request, HttpServletResponse response) {

		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) token.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
		AccessToken accessToken = session.getToken();

		/*
		 * PlainJWT plainJWT;
		 * 
		 * String tokenString = principal.toString();
		 * 
		 * try { plainJWT = PlainJWT.parse(tokenString); } catch
		 * (java.text.ParseException e) { System.out.println("Could not parse token"); }
		 * 
		 * // AccessToken.Access access = accessToken.getRealmAccess(); // Set<String>
		 * roles = access.getRoles();
		 */
		Collection<GrantedAuthority> roles = token.getAuthorities();

		for (String role : token.getAccount().getRoles()) {
			System.out.println("===========+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Role : " + role);
		}

		String userName = accessToken.getPreferredUsername();
		UserEntity userDTO = userService.findByUserName(userName);

		return userDTO;
	}

	/**
	 * Logout a user
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response) {

		UserEntity userDTO = null;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		/*
		 * if (auth != null) { new SecurityContextLogoutHandler().logout(request,
		 * response, auth); }
		 */
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {

			if (auth instanceof KeycloakAuthenticationToken) {
				KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder
						.getContext().getAuthentication();

				Principal principal = (Principal) authentication.getPrincipal();

				if (principal instanceof KeycloakPrincipal) {

					KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;

					AccessToken token = kPrincipal.getKeycloakSecurityContext().getToken();
					userDTO = userService.findByUserName(token.getPreferredUsername());
					if (userDTO != null) {
						userDTO.setPublicSecret(null);
						userDTO.setIsLoggedIn(0);
						userService.saveUser(userDTO);
					}

					Keycloak kc = Keycloak.getInstance("https://www.zdslogic.com/keycloak/auth", "zdslogic",
							"richard.campion", "ArcyAdmin8246+", "admin-cli");

					// Get realm
					RealmResource realmResource = kc.realm("zdslogic");
					UsersResource usersResource = realmResource.users();
					// RealmRepresentation realmRep = realmResource.toRepresentation();
					// realmRep.getSess

					// AssertEvents events = new AssertEvents(this);
					String userId = kPrincipal.getName();
					// List<UserSessionRepresentation> usr =
					// kc.realm("realmId").users().get("userId").getUserSessions();
					// List<UserSessionRepresentation> usr =
					// usersResource.get(userId).getUserSessions();
					// HttpSession session = request.getSession();
					// List<UserSessionModel> sessions = session.sessions().getUserSessions(realm,
					// user);
					// for (UserSessionRepresentation rep : usr) {
					// realmResource.deleteSession(rep.getUserId().get);
					// }
					UserResource user = usersResource.get(userId);
					user.logout();
				}
			}

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		Message<UserEntity> message = new Message();
		Node<UserEntity> node = new Node<UserEntity>(userDTO);
		message.setData(node);
		message.setMessage("Logged Out");

		// send a message to subscribed apps that user has Logged Out
		if (userDTO != null) {
			System.out.println(userDTO.getFirstName() + " " + userDTO.getLastName());
			System.out.println("Sending Logout Message");
			//webSocket.convertAndSend("/topic/user/auth", message);
		}
	}

	/**
	 * Authentication for every request - Triggered by every http request except the
	 * authentication
	 * 
	 * @see com.rkc.zds.config.security.XAuthTokenFilter Set the authenticated user
	 *      in the Spring Security context
	 * @param userName userName
	 */
	public void tokenAuthentication(String userName) {
		UserDetails details = userDetailsService.loadUserByUsername(userName);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details,
				details.getPassword(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	public UserEntity findByUserName(String login) {
		UserEntity userDTO = userService.findByUserName(login);
		return userDTO;
	}

}
