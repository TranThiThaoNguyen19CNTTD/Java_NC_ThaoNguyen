package net.codejava.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;
import net.codejava.User;
import net.codejava.Repository.UserRepository;

@Service
public class UserServices {

	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	public List<User> listAll() {
		return repo.findAll();
	}

	public void register(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		user.setEnabled(false);

		repo.save(user);

		sendVerificationEmail(user, siteURL);
	}

	private void sendVerificationEmail(User user, String siteURL)
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = "thaonguyentran9701@gmail.com";
		String senderName = "*_____CODE JAVA CUỐI KÌ_____*";
		String subject = "Vui lòng xác minh đăng ký của bạn";
		String content = "Dear [[name]]!<br>" + "Vui lòng nhấn vào liên kết bên dưới để xác minh đăng ký của bạn:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">Nhấn vào đây</a></h3>" + "Thank you!<br>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", user.getFullName());
		String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

		System.out.println("Email đã được gửi");
	}

	public boolean verify(String verificationCode) {
		User user = repo.findByVerificationCode(verificationCode);

		if (user == null || user.isEnabled()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setEnabled(true);
			repo.save(user);

			return true;
		}

	}

}
