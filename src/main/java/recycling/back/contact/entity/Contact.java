package recycling.back.contact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import recycling.back.contact.dto.ContactDto;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inquiryType;

    private String name;

    private String email;

    private String phone;

    private String company;

    private String content;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean agreeToTerms;

    @CreatedDate
    private LocalDateTime createDate;

    public Contact() {}

    public static Contact contact(ContactDto contactDto){
        return new Contact(contactDto);
    }

    private Contact(ContactDto contactDto){
        this.name = contactDto.getName();
        this.email = contactDto.getEmail();
        this.phone = contactDto.getPhone();
        this.company = contactDto.getCompany();
        this.content = contactDto.getContent();
        this.inquiryType = contactDto.getInquiryType();
        this.agreeToTerms = contactDto.getAgreeToTerms();
    }

    public Long getId() {
        return id;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    public String getContent() {
        return content;
    }

    public boolean getAgreeToTerms() {
        return agreeToTerms;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }
}
