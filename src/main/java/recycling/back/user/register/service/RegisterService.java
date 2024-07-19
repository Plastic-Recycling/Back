package recycling.back.user.register.service;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recycling.back.exception.DifferentCachedInfoException;
import recycling.back.exception.EmailDuplicateException;
import recycling.back.exception.NotInitialEmailException;
import recycling.back.user.register.dto.RegisterUser;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@Transactional
public class RegisterService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterService(JavaMailSender mailSender, UserRepository userRepository, CacheManager cacheManager, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean verifyEmail(String email) {
        String secureCode = checkToken(email);
        if(secureCode != null){
            sendEmail(email, secureCode);
            return false;
        }
        checkEmailDuplication(email);
        String token = generateTokenAndCaching(email);
        String confirmationUrl = String.format("http://localhost:8080/register/confirm?email=%s&token=%s",
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                URLEncoder.encode(token, StandardCharsets.UTF_8));
        sendEmail(email, confirmationUrl);
        return true;
    }

    private String checkToken(String email){
        Cache cache = cacheManager.getCache("verifiedEmail");
        if(cache != null && cache.get(email, String.class) != null){
            return cache.get(email, String.class);
        }
        return null;
    }

    private String generateTokenAndCaching(String email){
        String token = UUID.randomUUID().toString();
        Cache cache = cacheManager.getCache("verifyToken");
        if(cache != null && cache.get(email) == null){
            cache.put(email, token);
            return token;
        }
        throw new DuplicateRequestException("이미 완료된 요청입니다. 이메일을 확인해 주세요");
    }

    private void sendEmail(String email, String body) {
        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Email Validation");
            message.setText(body);
            mailSender.send(message);
        }catch(MailSendException ms){
            throw new MailSendException("메시지 전송 실패");
        }
    }

    private void checkEmailDuplication(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new EmailDuplicateException();
        }
    }

    public String confirmEmail(String email, String token) {
        if(checkToken(email, token)){
            String secureRandom = secureRandom();
            confirmEmailCached(email, secureRandom);
            return String.format("http://localhost:5173/complete-registration?email=%s&token=%s"
                    , URLEncoder.encode(email, StandardCharsets.UTF_8)
                    , URLEncoder.encode(secureRandom, StandardCharsets.UTF_8));
        }else if(isEmailValid(email, token)){
            return String.format("http://localhost:5173/complete-registration?email=%s&token=%s"
                    , URLEncoder.encode(email, StandardCharsets.UTF_8)
                    , URLEncoder.encode(token, StandardCharsets.UTF_8));
        }
        else{
            throw new DifferentCachedInfoException();
        }
    }

    private boolean checkToken(String email, String token) {
        Cache cache = cacheManager.getCache("verifyToken");
        if(cache != null){
            String cachedToken = cache.get(email, String.class);
            if(token.equals(cachedToken)){
                cache.evict(email);
                return true;
            }
        }
        return false;
    }

    private void confirmEmailCached(String email, String secureRandom) {
        Cache cache = cacheManager.getCache("verifiedEmail");
        if(cache != null){
            cache.put(email, secureRandom);
        }
    }

    private String secureRandom(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public void completeRegistration(RegisterUser registerUser) {
        if (!isEmailValid(registerUser.getEmail(), registerUser.getToken())){
            throw new NotInitialEmailException();
        }
        registration(registerUser);
    }

    private boolean isEmailValid(String email, String token) {
        Cache cache = cacheManager.getCache("verifiedEmail");
        return cache != null && token.equals(cache.get(email, String.class));
    }

    private void registration(RegisterUser registerUser){
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        userRepository.save(User.registration(registerUser));

        Cache cache = cacheManager.getCache("verifiedEmail");
        if(cache != null){
            cache.evict(registerUser.getEmail());
        }
    }

}
