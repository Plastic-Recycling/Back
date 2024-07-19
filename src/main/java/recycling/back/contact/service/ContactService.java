package recycling.back.contact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recycling.back.contact.dto.ContactDto;
import recycling.back.contact.entity.Contact;
import recycling.back.contact.repository.ContactRepository;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public void contact(ContactDto contactDto){
        contactDetails(contactDto);
    }

    private void contactDetails(ContactDto contactDto){
        Contact contact = Contact.contact(contactDto);
        contactRepository.save(contact);
    }
}
