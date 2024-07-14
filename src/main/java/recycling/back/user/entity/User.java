package recycling.back.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import recycling.back.user.dto.RegisterUser;

@Entity
public class User {
    @Id
    private Long id;

    private String email;

    private String name;

    private String username;

    private String password;

    public User() {}

    public static User registration(RegisterUser registerUser){
        return new User(registerUser);
    }

    private User(RegisterUser registerUser){
        this.email = registerUser.getEmail();
        this.name = registerUser.getName();
        this.username = registerUser.getUsername();
        this.password = registerUser.getPassword();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
