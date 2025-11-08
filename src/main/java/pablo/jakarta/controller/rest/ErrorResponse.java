package pablo.jakarta.controller.rest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ErrorResponse {
    private String error;
    private String message;
    private int status;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
