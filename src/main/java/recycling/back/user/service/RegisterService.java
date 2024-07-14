package recycling.back.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import recycling.back.user.dto.RegisterUser;
import recycling.back.user.entity.User;
import recycling.back.user.repository.UserRepository;

import java.util.UUID;

@Service
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

    public void verifyEmail(String email) {
        checkEmailDuplication(email);
        String token = generateTokenAndCaching(email);
        sendEmail(email, token);
    }

    private String generateTokenAndCaching(String email){
        String token = UUID.randomUUID().toString();
        Cache cache = cacheManager.getCache("verifyToken");
        if(cache != null){
            cache.put(email, token);
        }
        return token;
    }

    private void sendEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Validation");
        message.setText("http://localhost:8080/register/confirm?email=" + email + "&token=" + token);
        mailSender.send(message);
    }

    private void checkEmailDuplication(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException();
        }
    }

    public void confirmEmail(String email, String token) {
        boolean check = checkToken(email, token);
        if(check){
            confirmEmailCached(email);
        }else{
            throw new RuntimeException();
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

    private void confirmEmailCached(String email) {
        Cache cache = cacheManager.getCache("verifiedEmail");
        if(cache != null){
            cache.put(email, true);
        }
    }

    public void completeRegistration(RegisterUser registerUser) {
        if (!isEmailValid(registerUser.getEmail())){
            throw new RuntimeException();
        }
        registration(registerUser);
    }

    private boolean isEmailValid(String email) {
        Cache cache = cacheManager.getCache("verifiedEmail");
        return cache != null && Boolean.TRUE.equals(cache.get(email, Boolean.class));
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
