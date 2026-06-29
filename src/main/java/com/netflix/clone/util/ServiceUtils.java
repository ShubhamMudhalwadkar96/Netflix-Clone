package com.netflix.clone.util;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dao.VideoRepository;
import com.netflix.clone.entity.User;
import com.netflix.clone.entity.Video;
import com.netflix.clone.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+email));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: "+userId));

    }

    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with videoId: "+videoId));
    }
}
