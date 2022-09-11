package antifraud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transaction {
    @Min(1)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int amount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TransactionResult result;

    public void calculateResult() {
        if(amount <= 200) {
            result = TransactionResult.ALLOWED;
        } else if(amount > 200 && amount <= 1500) {
            result = TransactionResult.MANUAL_PROCESSING;
        } else if(amount > 1500) {
            result = TransactionResult.PROHIBITED;
        }
    }
}
