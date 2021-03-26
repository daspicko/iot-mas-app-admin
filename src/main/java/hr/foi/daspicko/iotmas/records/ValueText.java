package hr.foi.daspicko.iotmas.records;

import lombok.Data;

@Data
public class ValueText {

    private String value;
    private String text;

    public ValueText() {
    }

    public ValueText(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
