package com.example.Klein.controller;

import com.example.Klein.model.FriendRequest;
import com.example.Klein.model.User;
import com.example.Klein.repository.FriendRequestRepository;
import com.example.Klein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/friends")
@CrossOrigin
public class FriendController {
    @Autowired private FriendRequestRepository friendRepo;
    @Autowired private UserRepository userRepo;

    @PostMapping("/add")
    public String sendRequest(@RequestParam Long senderId, @RequestParam String friendUsername) {
        User sender = userRepo.findById(senderId).get();
        User receiver = userRepo.findByUsername(friendUsername).orElse(null);

        if (receiver == null) return "Không tìm thấy người dùng này.";
        if (sender.getId().equals(receiver.getId())) return "Không thể kết bạn với chính mình.";

        FriendRequest req = new FriendRequest();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setStatus("PENDING");
        friendRepo.save(req);

        return "Đã gửi lời mời!";
    }

    // 2. Chấp nhận kết bạn
    @PostMapping("/accept/{requestId}")
    public String acceptRequest(@PathVariable Long requestId) {
        FriendRequest req = friendRepo.findById(requestId).orElse(null);
        if (req != null) {
            req.setStatus("ACCEPTED"); // Chuyển thành bạn bè chính thức
            friendRepo.save(req);
            return "Đã chấp nhận kết bạn!";
        }
        return "Lỗi: Không tìm thấy lời mời.";
    }
    @GetMapping("/sent/{userId}")
    public List<FriendRequest> getSentRequests(@PathVariable Long userId) {
        User sender = userRepo.findById(userId).orElse(null);
        return friendRepo.findBySenderAndStatus(sender, "PENDING");
    }
    // 3. Lấy danh sách lời mời
    @GetMapping("/requests/{userId}")
    public List<FriendRequest> getRequests(@PathVariable Long userId) {
        User receiver = userRepo.findById(userId).orElse(null);
        return friendRepo.findByReceiverAndStatus(receiver, "PENDING");
    }

    @DeleteMapping("/cancel/{requestId}")
    public String cancelRequest(@PathVariable Long requestId) {
        try {
            friendRepo.deleteById(requestId);
            return "Đã hủy/từ chối lời mời.";
        } catch (Exception e) {
            return "Lỗi: Không tìm thấy lời mời để hủy.";
        }
    }

    @PostMapping("/unfriend")
    public String unfriend(@RequestParam Long userId, @RequestParam Long friendId) {
        // Tìm mối quan hệ giữa 2 người
        FriendRequest relation = friendRepo.findFriendship(userId, friendId);

        if (relation != null) {
            friendRepo.delete(relation); // Xóa khỏi Database
            return "Đã hủy kết bạn.";
        }
        return "Lỗi: Không tìm thấy mối quan hệ bạn bè.";
    }
}

