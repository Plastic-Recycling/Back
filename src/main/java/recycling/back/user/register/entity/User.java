package recycling.back.user.register.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import recycling.back.recycle.entity.RecycleCount;
import recycling.back.user.register.dto.RegisterUser;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "recycle_count_id", referencedColumnName = "id")
    private RecycleCount recycleCount;


    public User() {}

    public static User registration(RegisterUser registerUser){
        return new User(registerUser);
    }

    private User(RegisterUser registerUser){
        this.email = registerUser.getEmail();
        this.name = registerUser.getName();
        this.username = registerUser.getUsername();
        this.password = registerUser.getPassword();
        this.recycleCount = new RecycleCount();
    }

    public void addRole(Role role){
        this.roles.add(role);
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

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public RecycleCount getRecycleCount() {
        return recycleCount;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
