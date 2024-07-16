package recycling.back.user.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import recycling.back.user.register.entity.User;

import java.time.Instant;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean isValid = true;

    public RefreshToken() {
    }

    public static RefreshToken create(User user, String token, Instant expiryDate) {
        return new RefreshToken(user, token, expiryDate);
    }

    private RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public void invalidate() {
        this.isValid = false;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public boolean isValid() {
        return isValid;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
