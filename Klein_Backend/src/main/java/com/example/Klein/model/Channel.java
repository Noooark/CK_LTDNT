package com.example.Klein.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "channels")
@Data
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // 👇 THÊM DÒNG NÀY
    private String type; // "TEXT" hoặc "VOICE"

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;
}