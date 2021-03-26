package hr.foi.daspicko.iotmas.records;

import lombok.Data;

@Data
public class SshCommandResult {

    private int statusCode;
    private String result;

    public SshCommandResult() {
    }

    public SshCommandResult(int statusCode, String result) {
        this.statusCode = statusCode;
        this.result = result;
    }
}
