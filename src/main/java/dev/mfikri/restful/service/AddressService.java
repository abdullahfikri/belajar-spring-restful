package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.AddressResponse;
import dev.mfikri.restful.model.CreateAddressRequest;
import dev.mfikri.restful.model.UpdateAddressRequest;

import java.util.List;

public interface AddressService {
    AddressResponse create(User user, CreateAddressRequest request);
    AddressResponse get(User user, String contactId, String addressId);
    AddressResponse update(User user, UpdateAddressRequest request);
    void delete(User user, String contactId, String addressId);
    List<AddressResponse> list(User user, String contactId);
}
