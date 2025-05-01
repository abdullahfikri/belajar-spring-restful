package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.ContactResponse;
import dev.mfikri.restful.model.CreateContactRequest;
import dev.mfikri.restful.model.SearchContactRequest;
import dev.mfikri.restful.model.UpdateContactRequest;
import org.springframework.data.domain.Page;

public interface ContactService {
    ContactResponse create(User user, CreateContactRequest request);
    ContactResponse get(User user, String contactId);
    ContactResponse update(User user, UpdateContactRequest request);
    void delete(User user, String contactId);

    Page<ContactResponse> search(User user, SearchContactRequest request);
}
