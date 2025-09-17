package org.example.ailifelegacy.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
    private int status;
    private String message;
    private T data;

    // 200 OK with body
    public static <T> SuccessResponse<T> of(T data) {
        return SuccessResponse.<T>builder()
            .status(HttpStatus.OK.value())
            .message("success")
            .data(data)
            .build();
    }

    // 201 Created with body
    public static <T> SuccessResponse<T> created(T data) {
        return SuccessResponse.<T>builder()
            .status(HttpStatus.CREATED.value())
            .message("created")
            .data(data)
            .build();
    }

    // 201 Created without body
    public static SuccessResponse<Void> created() {
        return SuccessResponse.<Void>builder()
            .status(HttpStatus.CREATED.value())
            .message("created")
            .data(null)
            .build();
    }
}
