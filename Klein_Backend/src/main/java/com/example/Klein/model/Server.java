package com.example.Klein.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "servers") // Trỏ vào bảng 'servers' trong MySQL
@Data
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Server thuộc về một người tạo (Owner)
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "icon_url")
    private String iconUrl;
}
