package antifraud.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class SetAccessReq {
    public enum Operation {
        LOCK,
        UNLOCK
    }

    @NotBlank
    private String username;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Operation operation;
}
