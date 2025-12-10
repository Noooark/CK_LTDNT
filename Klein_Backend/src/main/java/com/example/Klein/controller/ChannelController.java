package com.example.Klein.controller;

import com.example.Klein.model.Channel;
import com.example.Klein.model.Server;
import com.example.Klein.repository.ChannelRepository;
import com.example.Klein.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@CrossOrigin
public class ChannelController {

    @Autowired private ChannelRepository channelRepo;
    @Autowired private ServerRepository serverRepo;

    // 1. Lấy danh sách kênh của 1 Server
    @GetMapping("/server/{serverId}")
    public List<Channel> getChannelsByServer(@PathVariable Long serverId) {
        return channelRepo.findByServerId(serverId);
    }

    // 2. Tạo kênh mới
    @PostMapping("/create")
    public Channel createChannel(@RequestParam String name, @RequestParam String type, @RequestParam Long serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server == null) return null;

        Channel channel = new Channel();
        channel.setName(name);
        channel.setType(type); // "TEXT" hoặc "VOICE"
        channel.setServer(server);

        return channelRepo.save(channel);
    }
}