package com.tweetapp.service.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.client.TweetServiceFeignClient;
import com.tweetapp.dao.UserRepository;
import com.tweetapp.entity.User;
import com.tweetapp.exception.BusinessException;
import com.tweetapp.exception.ExceptionConstants;
import com.tweetapp.models.JwtRequest;
import com.tweetapp.models.JwtResponse;
import com.tweetapp.models.Mail;
import com.tweetapp.models.ResetPasswordDto;
import com.tweetapp.models.TweetDto;
import com.tweetapp.utils.JwtTokenUtil;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserServiceImpl userDetailsService;

	@Autowired
	TweetServiceFeignClient tweetServiceFeignClient;

	@Autowired
	@Qualifier("mailsender")
	private JavaMailSenderImpl emailSender;

	@Autowired
	@Qualifier("redisCacheManager")
	RedisCacheManager redisCacheManager;

	@Value("${email.service.emailId}")
	private String fromEmail;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found for email " + username);
		}

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				new ArrayList<>());
	}

	public User registerUser(User userDto) {
		User user = userRepository.findByEmail(userDto.getEmail());
		if (Objects.nonNull(userDto) && Objects.isNull(user)) {
			userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
			user = userRepository.save(userDto);
		} else {
			logger.error("Error in UserServiceImpl:: registerUser():: User Id already Presenet.");
			throw new BusinessException(ExceptionConstants.USER_ID_ALREADY_PRESENT, ExceptionConstants.GENERAL_MODULE,
					"Email Already Exists");
		}
		return user;
	}

	public JwtResponse login(JwtRequest authenticationRequest) {
		try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		} catch (DisabledException e) {
			throw new BusinessException(ExceptionConstants.USER_ID_DISABLED, ExceptionConstants.GENERAL_MODULE,
					"Email Already Exists");
		} catch (BadCredentialsException e) {
			throw new BusinessException(ExceptionConstants.INVALID_CREDENTIALS, ExceptionConstants.GENERAL_MODULE,
					"Email Already Exists");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);
		User user = userRepository.findByEmail(authenticationRequest.getUsername());

		JwtResponse jwtResponse = new JwtResponse(token, user);
		if (Objects.nonNull(jwtResponse)) {
			user.setStatus("loggedin");
			userRepository.save(user);
		}
		return jwtResponse;
	}

	private void authenticate(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new BusinessException(ExceptionConstants.USER_ID_DISABLED, ExceptionConstants.GENERAL_MODULE,
					"Email Already Exists");
		} catch (BadCredentialsException e) {
			throw new BusinessException(ExceptionConstants.INVALID_CREDENTIALS, ExceptionConstants.GENERAL_MODULE,
					"Email Already Exists");
		}
	}

	public void logout(String userName) {
		User user = userRepository.findByEmail(userName);
		if (Objects.nonNull(user)) {
			user.setStatus("loggedout");
			userRepository.save(user);
		}

	}

	public void forgetPassword(String userName, String otp) {
		String emailId = userName;
		String content = "<table style=\"width:100%\";>\r\n" + "	<tr>\r\n"
				+ "		<td style=\"text-align:right\">\r\n"
				+ "			<img width=\"30%\" src='cid:1234'>  </td>\r\n" + "		</tr>\r\n" + "		<tr>\r\n"
				+ "			<td>\r\n"
				+ "				<p style=\"font-size: 16px;line-height: 24px;font-weight: 500;text-align: justify;\">Use the authorization code to access your application.</p>\r\n"
				+ "				<p style=\"font-size: 16px;line-height: 24px;font-weight: 500;text-align: justify;\">For account security, please use the following authorization code to access your application: <b style=font-size: 17px;>\r\n"
				+ "						" + otp + "  </b>\r\n" + "				</p>\r\n"
				+ "				<p style=\"font-size: 16px;line-height: 24px;font-weight: 500;text-align: justify;\">Please do not share your code with anyone.</p>\r\n"
				+ "			</td>\r\n" + "		</tr>\r\n" + "		<tr>\r\n"
				+ "			<td style=\"height:20px;border-bottom:1px solid #DCDCDC;\"/>\r\n" + "		</tr>\r\n"
				+ "		<tr>\r\n" + "			<td style=\"height:10px;\"/>\r\n" + "		</tr>\r\n"
				+ "		<tr>\r\n" + "			<td>\r\n"
				+ "				<img width=\"100\" src='cid:5678'>  </td>\r\n" + "			</tr>\r\n"
				+ "		</table>";

		Mail mail = new Mail();
		mail.setTo(emailId);
		mail.setSubject("");
		mail.setContent(content);
		if (emailId != null) {
			sendEmail(mail);
		}

	}

	@Cacheable(value = "otpcache", key = "#userName")
	public String generateOTP(String userName) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public void sendEmail(Mail mail) {
		emailSender.send(new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws MessagingException, FileNotFoundException {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				message.setFrom(fromEmail);
				if (mail.getTo().contains(", ")) {
					message.setTo(InternetAddress.parse(mail.getTo()));
				} else {
					message.setTo(mail.getTo());
				}
				message.setSubject(mail.getSubject());
				message.setText(mail.getContent(), true);
			}
		});

	}

	public Boolean verifyOTP(String userName, String otp) {

		// get value from redis cache using key as loanKey
		String cacheOtp = getValueByKeyFromCache("otpcache", userName);
		if (cacheOtp != null && cacheOtp.equals(otp)) {
			evictCacheByKey("otpcache", userName);
			return true;
		} else {
			throw new BusinessException(ExceptionConstants.VERIFY_OTP, ExceptionConstants.GENERAL_MODULE,
					"Error occured while verifying OTP.");
		}
	}

	/*
	 * get the value from cache by key
	 */
	public String getValueByKeyFromCache(String cacheName, String cacheKey) {
		logger.debug("PPPBusinessServiceImpl:: getValueByKeyFromCache():: start");
		String value = null;
		value = redisCacheManager.getCache(cacheName).get(cacheKey) != null
				? redisCacheManager.getCache(cacheName).get(cacheKey).get().toString()
				: null;
		logger.debug("PPPBusinessServiceImpl:: getValueByKeyFromCache():: exit");
		return value;
	}

	/*
	 * delete value from cache by key
	 */

	public void evictCacheByKey(String cacheName, String cacheKey) {
		logger.debug("PPPBusinessServiceImpl:: evictCacheByKey():: start");
		redisCacheManager.getCache(cacheName).evictIfPresent(cacheKey);
	}

	public User reset(ResetPasswordDto resetPasswordDto, String userName) {
		String status = null;
		User userResponse = null;
		if (Objects.nonNull(resetPasswordDto) && Objects.nonNull(resetPasswordDto.getNewPassword())
				&& Objects.nonNull(resetPasswordDto.getConfirmPassword())
				&& resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {

			User user = userRepository.findByEmail(userName);
			if (Objects.nonNull(user)) {
				user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
				userResponse = userRepository.save(user);
				if (Objects.nonNull(userResponse)) {
					status = "Password Changed";
				} else {
					status = "Failed to updated Password";
				}
			}
		}

		return userResponse;
	}

	public List<User> getAllUsers() {
		List<User> userList = userRepository.findAll();
		if (Objects.nonNull(userList)) {
			return userList;
		} else {
			logger.error("Error in UserServiceImpl:: getAllUsers():: Empty List.");
			throw new BusinessException(ExceptionConstants.USER_LIST_EMPTY, ExceptionConstants.GENERAL_MODULE,
					"Empty List");
		}

	}

	public String postNewTweet(TweetDto tweetDto, String userName) {
		if (Objects.nonNull(tweetDto) && !StringUtils.isAllEmpty(tweetDto.getTweet())) {
			if (tweetDto.getTweet().length() <= 100) {
				return tweetServiceFeignClient.postNewTweet(tweetDto, userName).getBody();
			} else {
				logger.error("Error in UserServiceImpl:: getAllUsers():: Empty List.");
				throw new BusinessException(ExceptionConstants.TWEET_LENGTH, ExceptionConstants.GENERAL_MODULE,
						"Tweet is empty");
			}

		} else {
			logger.error("Error in UserServiceImpl:: getAllUsers():: Empty List.");
			throw new BusinessException(ExceptionConstants.TWEET_EMPTY, ExceptionConstants.GENERAL_MODULE,
					"Tweet is empty");
		}
	}

	public String updateTweet(TweetDto tweetDto, String userName, int id) {
		if (Objects.nonNull(tweetDto) && !StringUtils.isAllEmpty(tweetDto.getTweet())) {
			if (tweetDto.getTweet().length() <= 100) {
				return tweetServiceFeignClient.updateTweet(tweetDto, userName, id).getBody();
			} else {
				logger.error("Error in UserServiceImpl:: getAllUsers():: Empty List.");
				throw new BusinessException(ExceptionConstants.TWEET_LENGTH, ExceptionConstants.GENERAL_MODULE,
						"Tweet is empty");
			}

		} else {
			logger.error("Error in UserServiceImpl:: getAllUsers():: Empty List.");
			throw new BusinessException(ExceptionConstants.TWEET_EMPTY, ExceptionConstants.GENERAL_MODULE,
					"Tweet is empty");
		}
	}

	public String deleteTweet(TweetDto tweetDto, String userName, int id) {
		return tweetServiceFeignClient.deleteTweet(tweetDto, userName, id).getBody();
	}

	public List<TweetDto> getUserTweets(String userName) {
		List<TweetDto> tweetList= tweetServiceFeignClient.getUserTweets(userName).getBody();
		if(Objects.nonNull(tweetList)) {
			return tweetList;
		}else {
			throw new BusinessException(ExceptionConstants.NO_TWEETS_BY_USER, ExceptionConstants.GENERAL_MODULE,
					"Tweets Not Available");
		}
	}

	public List<TweetDto> getAllTweets() {
		List<TweetDto> tweetList= tweetServiceFeignClient.getAllTweets().getBody();
		if(Objects.nonNull(tweetList)) {
			return tweetList;
		}else {
			throw new BusinessException(ExceptionConstants.NO_TWEETS_BY_USER, ExceptionConstants.GENERAL_MODULE,
					"Tweets Not Available");
		}
	}

}
