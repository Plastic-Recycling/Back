package recycling.back.recycle.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class MountPlastic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal pp;

    private BigDecimal ps;

    private BigDecimal pe;

    @CreatedDate
    private LocalDate updateDate;

    public MountPlastic(){}

    public static MountPlastic update(BigDecimal pp, BigDecimal pe, BigDecimal ps){
        return new MountPlastic(pp, pe, ps);
    }

    private MountPlastic(BigDecimal pp, BigDecimal pe, BigDecimal ps){
        this.pp = pp;
        this.pe = pe;
        this.ps = ps;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPp() {
        return pp;
    }

    public BigDecimal getPs() {
        return ps;
    }

    public BigDecimal getPe() {
        return pe;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }
}
