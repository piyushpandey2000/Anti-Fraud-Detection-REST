package antifraud.request;

import antifraud.models.ERole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class EditRoleReq {

    @NotBlank
    private String username;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private ERole role;
}
