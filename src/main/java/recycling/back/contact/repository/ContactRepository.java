package recycling.back.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import recycling.back.contact.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
