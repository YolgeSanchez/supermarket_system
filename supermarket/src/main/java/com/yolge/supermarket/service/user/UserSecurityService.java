package com.yolge.supermarket.service.user;

import com.yolge.supermarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService {
    private final UserRepository userRepository;

    public boolean isAccountOwner(String username, Long id) {
        return userRepository.existsByUsernameAndIdAndDeletedAtIsNull(username, id);
    }
}
