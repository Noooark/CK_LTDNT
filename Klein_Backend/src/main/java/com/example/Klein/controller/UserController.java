package com.example.Klein.controller;

import com.example.Klein.dto.request.UserUpdateRequest;
import com.example.Klein.model.FriendRequest;
import com.example.Klein.model.User;
import com.example.Klein.repository.FriendRequestRepository;
import com.example.Klein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users") // <-- Khớp với đường dẫn Frontend gọi
@CrossOrigin
public class UserController {

    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // API lấy danh sách bạn bè
    // URL: /api/users/friends/{userId}
    @GetMapping("/friends/{userId}")
    public List<User> getFriendsList(@PathVariable Long userId) {

        // 1. Lấy tất cả các mối quan hệ đã ACCEPTED
        List<FriendRequest> friendships = friendRequestRepository.findAllAcceptedFriendships(userId);

        List<User> friends = new ArrayList<>();

        // 2. Lọc ra người kia là ai
        for (FriendRequest req : friendships) {
            if (req.getSender().getId().equals(userId)) {
                // Nếu mình là người gửi -> Bạn là người nhận
                friends.add(req.getReceiver());
            } else {
                // Nếu mình là người nhận -> Bạn là người gửi
                friends.add(req.getSender());
            }
        }

        return friends; // Trả về danh sách User (bạn bè)
    }
    @PutMapping("/{userId}/avatar")
    public ResponseEntity<?> updateUserAvatar(@PathVariable Long userId, @RequestParam String avatarUrl) {
        return userRepo.findById(userId).map(user -> {
            user.setAvatarUrl(avatarUrl); // Cập nhật cột avatar_url
            userRepo.save(user);          // Lưu lại
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestParam String status) {
        return userRepo.findById(userId).map(user -> {
            user.setStatus(status); // Cập nhật vào DB
            userRepo.save(user);

            // 🔥 QUAN TRỌNG: Bắn tin qua WebSocket để App cập nhật chấm xanh ngay lập tức
            // Gửi cả ID và Status mới ra kênh công khai "/topic/status"
            messagingTemplate.convertAndSend("/topic/status", user);

            return ResponseEntity.ok("Đã cập nhật trạng thái: " + status);
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUserInfo(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        // 1. Tìm User
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy người dùng.");
        }

        // 2. Cập nhật thông tin
        // Có thể thêm logic kiểm tra trùng username/email ở đây nếu cần
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // 3. Lưu vào Database
        userRepo.save(user);

        return ResponseEntity.ok("Thông tin hồ sơ đã được cập nhật thành công.");
    }
}