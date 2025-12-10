package com.example.Klein.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages") // 👈 Trỏ vào bảng 'messages' có sẵn
@Data
public class ChannelMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    // Nếu bảng messages chưa có cột 'type', bạn cần chạy SQL thêm vào
    // ALTER TABLE messages ADD COLUMN type VARCHAR(20) DEFAULT 'TEXT';
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id") // Bảng messages dùng cột 'user_id'
    private User sender;

    @ManyToOne
    @JoinColumn(name = "channel_id") // Bảng messages dùng cột 'channel_id'
    private Channel channel;

    // Bảng messages dùng cột 'created_at' hay 'timestamp'?
    // Kiểm tra ảnh bạn gửi: Cột tên là 'created_at'
    @Column(name = "created_at")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    private Integer duration;
}