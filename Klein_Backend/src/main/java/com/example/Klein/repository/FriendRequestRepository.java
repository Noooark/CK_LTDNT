package com.example.Klein.repository;

import com.example.Klein.model.FriendRequest;
import com.example.Klein.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // 1. Tìm các lời mời đang chờ (đã có từ trước)
    List<FriendRequest> findByReceiverAndStatus(User receiver, String status);
    List<FriendRequest> findBySenderAndStatus(User sender, String status);
    // 2. [MỚI] Tìm tất cả bạn bè đã chấp nhận (Dù mình là người gửi hay người nhận)
    @Query("SELECT r FROM FriendRequest r WHERE (r.sender.id = :userId OR r.receiver.id = :userId) AND r.status = 'ACCEPTED'")
    List<FriendRequest> findAllAcceptedFriendships(Long userId);

    @Query("SELECT r FROM FriendRequest r WHERE (r.sender.id = :u1 AND r.receiver.id = :u2) OR (r.sender.id = :u2 AND r.receiver.id = :u1)")
    FriendRequest findFriendship(@Param("u1") Long user1Id, @Param("u2") Long user2Id);
}
