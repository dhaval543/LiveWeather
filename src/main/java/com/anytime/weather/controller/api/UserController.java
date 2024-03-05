package com.anytime.weather.controller.api;

import com.anytime.weather.Utils.AppConstants;
import com.anytime.weather.Utils.CacheManager;
import com.anytime.weather.Utils.Encryptor;
import com.anytime.weather.Utils.SendMail;
import com.anytime.weather.controller.request.UserRequest;
import com.anytime.weather.exception.EntityType;
import com.anytime.weather.exception.ExceptionType;
import com.anytime.weather.exception.TWMException;
import com.anytime.weather.model.User;
import com.anytime.weather.model.UserDTO;
import com.anytime.weather.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@RestController
public class UserController {
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private UserService userDetailsService;

	@Autowired
	private SendMail sendMail;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity saveUser(@RequestBody UserRequest user) throws Exception {
		User user1 = userDetailsService.findByUsername(user.getUsername());
		if (user1 != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exist");
		}
		return ResponseEntity.status(HttpStatus.OK).body(userDetailsService.save(user));
	}

	@RequestMapping(value = "/reset-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity resetPassword(@RequestBody UserRequest user, @RequestParam("userName") String userName) {
		// userService.setPassword(authDetails, false);
		UserDTO updateUser = new UserDTO();
		String userNameUpdate = Encryptor.decrypt(AppConstants.ENCRYPTION_KEY, AppConstants.ENCRYPTION_INIT_VECTOR,
				userName);
		updateUser.setUsername(userNameUpdate);
		updateUser.setPassword(user.getPassword());

		userDetailsService.updatePassword(updateUser);
		return ResponseEntity.ok().body("Password reset successfully");
	}

	@RequestMapping(value = "/check-link-expiration", method = RequestMethod.GET)
	public ResponseEntity isLinkExpire(@RequestParam("userName") String userName) {
		User user = (User) cacheManager.get(userName);
		if (user == null) {
			throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, "Your Link is expired.");
		}
		return ResponseEntity.ok().body("");
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
	public ResponseEntity forgotPassword(@RequestParam("username") String username,
			@RequestHeader("Origin") String clientHostName) throws Exception {
		User user = userDetailsService.findByUsername(username);
		if (user == null) {
			throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, username + " doesn't exists");
		}

		String userName = Encryptor.encrypt(AppConstants.ENCRYPTION_KEY, AppConstants.ENCRYPTION_INIT_VECTOR,
				user.getUsername());
		User userdto = new User();
		userdto.setUsername(user.getUsername());
		cacheManager.setlink(userName, userdto);
		try {
			userName = URLEncoder.encode(userName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
		}
		if (clientHostName == null) {
			sendMail.sendMail(user.getUsername(), "/#/verifyLink/?userName=" + userName);

		} else {
			sendMail.sendMail(user.getUsername(), clientHostName + "/#/verifyLink/?userName=" + userName);

		}
		return ResponseEntity.ok().body("Activation link has been sent to registered email address.");

	}

	/**
	 * Returns a new RuntimeException
	 *
	 * @param entityType
	 * @param exceptionType
	 * @param args
	 * @return
	 */
	private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
		return TWMException.throwException(entityType, exceptionType, args);
	}
}
