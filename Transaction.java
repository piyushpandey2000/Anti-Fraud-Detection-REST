package antifraud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Min(1)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int amount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String result;

    public Transaction(int amount) {
        this.amount = amount;
    }

    public void calculateResult() {
        if(amount <= 200) {
            result = "ALLOWED";
        } else if(amount > 200 && amount <= 1500) {
            result = "MANUAL_PROCESSING";
        } else if(amount > 1500) {
            result = "PROHIBITED";
        }
    }
}
