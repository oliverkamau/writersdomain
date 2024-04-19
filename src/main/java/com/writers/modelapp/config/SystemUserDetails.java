package com.writers.modelapp.config;


import com.writers.modelapp.setups.entity.Users;
import com.writers.modelapp.setups.repository.UserRepo;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Service
@Slf4j
public class SystemUserDetails implements UserDetailsService {


    @Value("${otp.timeout}")
    private String timeout;

    @Value("${custommail.host}")
    private String host;

    @Value("${custommail.port}")
    private String port;

    @Value("${custommail.username}")
    private String mailusername;

    @Value("${custommail.alias}")
    private String mailAlias;

    @Value("${custommail.password}")
    private String mailpassword;

    @Value("${custommail.smtpauth}")
    private boolean smtpAuth;

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;
    private final WebClient webClient;


    public SystemUserDetails(UserRepo userRepo, BCryptPasswordEncoder encoder, WebClient webClient) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.webClient = webClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println(username);
            Users user = userRepo.findByUsernameIgnoreCaseAndEnabled(username, "1");

            if (user == null) {
                throw new UsernameNotFoundException("Invalid Username or Password");
            }
            return new UserDetailsImpl(user);
        }
        catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }

    }

    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
//        log.info("OTP CODE GENERATED IS --->"+otp);
        return String.valueOf(otp);
    }

    public String promptOTP(Users user) {

        String otp =generateOTP();
        user.setOtp(otp);
        user.setOtpExpiry(new Date());
        userRepo.save(user);
        new Thread(
                () -> {
                    try {
                        mailOTP(user,otp);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }).start();

        return otp;
    }

    public void verifyOTP(Users user, String otp) throws Exception {

        if (user.getOtpExpiry().getTime() + Integer.parseInt(timeout) < System.currentTimeMillis()) {
            throw new Exception("OTP has expired!");
        }
        if(!otp.equalsIgnoreCase(user.getOtp())) {
            throw new Exception("OTP is invalid!");
        }


    }



    private void mailOTP(Users user, String token) throws MessagingException, UnsupportedEncodingException {

        log.info("SMTP AUTH :: {}",smtpAuth);

        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = null;
        if(smtpAuth){
            session = Session.getInstance(props,
                    new jakarta.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(mailusername, mailpassword);
                        }
                    });
        }else {
            session = Session.getInstance(props, new jakarta.mail.Authenticator() {});
        }

        log.info("Sending email to {}", user.getEmail());
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(mailusername,mailAlias));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            msg.setSubject("OTP LOGIN VERIFICATION");
            msg.setContent("<h2>OTP FOR LOGIN</h2>" +
                    "<h3 style=\"color:black\">Dear "+user.getName()+" Your One Time Verification Code is <span style=\"color:blue;font-weight: bold;\">"+token+"</span></h3> ", "text/html");
            msg.setSentDate(new Date());

            Transport.send(msg);
            log.info("-----Emails sent successfully to {}-----",user.getEmail());
        } catch (Exception e) {
            log.error("Emails not sent :: Error ",e);
        }

    }



}
