package dev.mfikri.restful.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 3, max = 100)
    private String name;

    @Size(min = 8, max = 100)
    private String password;
}
