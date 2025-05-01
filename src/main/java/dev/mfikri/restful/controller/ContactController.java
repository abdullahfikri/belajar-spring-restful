package dev.mfikri.restful.controller;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.*;
import dev.mfikri.restful.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping(path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){
        ContactResponse response = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping(path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
        ContactResponse response = contactService.get(user, contactId);
        return WebResponse.<ContactResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping(path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user,
                                               @RequestBody UpdateContactRequest request,
                                               @PathVariable("contactId") String contactId){
        request.setId(contactId);
        ContactResponse response = contactService.update(user, request);
        return WebResponse.<ContactResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId) {
        contactService.delete(user, contactId);

        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "phone", required = false) String phone,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        SearchContactRequest request = SearchContactRequest.builder()
                .page(page)
                .size(size)
                .name(name)
                .email(email)
                .phone(phone)
                .build();
        Page<ContactResponse> responses = contactService.search(user, request);
        return WebResponse.<List<ContactResponse>> builder()
                .data(responses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(responses.getNumber())
                        .totalPage(responses.getTotalPages())
                        .size(responses.getSize())
                        .build())
                .build();
    }

}
