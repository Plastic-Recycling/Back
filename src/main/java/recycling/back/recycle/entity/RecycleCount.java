package recycling.back.recycle.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import recycling.back.user.register.entity.User;

@Entity
public class RecycleCount {
    @Id
    private Long id;

    private int pe;

    private int pp;

    private int ps;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Long getId() {
        return id;
    }

    public int getPe() {
        return pe;
    }

    public int getPp() {
        return pp;
    }

    public int getPs() {
        return ps;
    }

    public User getUser() {
        return user;
    }

    public void ppCountUp(){
        this.pp += 1;
    }

    public void peCountUp(){
        this.pe += 1;
    }

    public void psCountUp(){
        this.ps += 1;
    }

}
