package lets.code.reactor.security;


import lets.code.reactor.pages.authorize.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private JWTUtil jwtUtil;
    private UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String username = jwtUtil.getUsernameFromToken(token);

        return userRepository.findByUsername(username)
                .flatMap(userDetails -> {
                    if(username.equals(userDetails.getUsername()) && jwtUtil.isTokenValidated(token)){
                        return Mono.just(authentication);
                    }else{
                        return Mono.empty();
                    }
                });
    }
}