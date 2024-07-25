package recycling.back.user.register.service;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recycling.back.exception.DifferentCachedInfoException;
import recycling.back.exception.EmailDuplicateException;
import recycling.back.exception.NotInitialEmailException;
import recycling.back.user.register.dto.RegisterUser;
import recycling.back.user.register.entity.Role;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.RoleRepository;
import recycling.back.user.register.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class RegisterService {

    @Value("${url.front}")
    private String frontUrl;

    @Value("${url.back}")
    private String backUrl;

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public RegisterService(JavaMailSender mailSender, UserRepository userRepository, CacheManager cacheManager, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public boolean verifyEmail(String email) {
        String secureCode = checkToken(email);
        if(secureCode != null){
            sendEmail(email, secureCode, true);
            return false;
        }
        checkEmailDuplication(email);
        String token = generateTokenAndCaching(email);
        String confirmationUrl = String.format(backUrl + "/register/confirm?email=%s&token=%s",
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                URLEncoder.encode(token, StandardCharsets.UTF_8));
        sendEmail(email, confirmationUrl, false);
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

    private void sendEmail(String email, String body, boolean isSecureCode) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("이메일 인증");

            String htmlContent;

            if (isSecureCode) {
                htmlContent = String.format(
                        "<html><body>" +
                                "<img src='https://axqdttmvdvr6.objectstorage.ap-chuncheon-1.oci.customer-oci.com/n/axqdttmvdvr6/b/bucket-20240724-1303/o/mailImage.png' alt='Email Verification' style='width:100%%; max-width:300px;'>" +
                                "<h1>이메일 인증 안내입니다.</h1>" +
                                "<p>귀하의 보안 코드는 다음과 같습니다: <strong>%s</strong></p>" +
                                "<p>이 코드를 인증 페이지에 입력해주세요.</p>" +
                                "</body></html>",
                        body
                );
            } else {
                htmlContent = String.format(
                        "<html><body>" +
                                "<img src='https://axqdttmvdvr6.objectstorage.ap-chuncheon-1.oci.customer-oci.com/n/axqdttmvdvr6/b/bucket-20240724-1303/o/mailImage.png' alt='Email Verification' style='width:100%%; max-width:300px'>" +
                                "<h1>이메일 인증 안내입니다.</h1>" +
                                "<p>아래 버튼을 클릭하여 이메일 인증을 완료해주세요</p>" +
                                "<a href='%s' style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>이메일 인증하기</a>" +
                                "</body></html>",
                        body
                );
            }

//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(email);
//            message.setSubject("Email Validation");
//            message.setText(body);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
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
            return String.format(frontUrl + "/complete-registration?email=%s&token=%s"
                    , URLEncoder.encode(email, StandardCharsets.UTF_8)
                    , URLEncoder.encode(secureRandom, StandardCharsets.UTF_8));
        }else if(isEmailValid(email, token)){
            return String.format(frontUrl + "/complete-registration?email=%s&token=%s"
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
        User user = User.registration(registerUser);

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(
                () -> new NoSuchElementException("not found"));
        user.addRole(userRole);
        userRepository.save(user);

        Cache cache = cacheManager.getCache("verifiedEmail");
        if(cache != null){
            cache.evict(registerUser.getEmail());
        }
    }

}
