package recycling.back.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import recycling.back.contact.dto.ContactDto;
import recycling.back.contact.service.ContactService;
import recycling.back.util.ResponseUtil;

@RestController
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> contact(@RequestBody ContactDto contactDto){
        contactService.contact(contactDto);
        return ResponseUtil.ok("success");
    }
}
