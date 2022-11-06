package antifraud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Min(1)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int amount;

    @NotBlank
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String ip;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String number;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TransactionResult result;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String info = "none";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Enumerated(value = EnumType.STRING)
    private Region region;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String date;

    public void calculateResult() {
        if(amount <= 200) {
            result = TransactionResult.ALLOWED;
        } else if(amount <= 1500) {
            result = TransactionResult.MANUAL_PROCESSING;
        } else {
            result = TransactionResult.PROHIBITED;
        }
    }
}
