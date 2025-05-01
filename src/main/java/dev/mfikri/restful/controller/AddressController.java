package dev.mfikri.restful.controller;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.AddressResponse;
import dev.mfikri.restful.model.CreateAddressRequest;
import dev.mfikri.restful.model.UpdateAddressRequest;
import dev.mfikri.restful.model.WebResponse;
import dev.mfikri.restful.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    @PostMapping(path = "/api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<AddressResponse> create(User user,
                                               @RequestBody CreateAddressRequest request,
                                               @PathVariable("contactId") String contactId) {
        request.setContactId(contactId);

        AddressResponse response = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
        AddressResponse response = addressService.get(user, contactId, addressId);

        return WebResponse.<AddressResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping(path = "/api/contacts/{contactId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @RequestBody UpdateAddressRequest request,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {
        request.setContactId(contactId);
        request.setAddressId(addressId);

        AddressResponse response = addressService.update(user, request);

        return WebResponse.<AddressResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
        addressService.delete(user, contactId, addressId);

        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(User user,
                                    @PathVariable("contactId") String contactId) {
        List<AddressResponse> responses = addressService.list(user, contactId);

        return WebResponse.<List<AddressResponse>>builder()
                .data(responses)
                .build();
    }
}
