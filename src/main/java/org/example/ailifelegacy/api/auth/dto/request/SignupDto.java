package org.example.ailifelegacy.api.auth.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignupDto {
    private String email;
    private String password;
}
