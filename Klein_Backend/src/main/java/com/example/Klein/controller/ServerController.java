package com.example.Klein.controller;

import com.example.Klein.model.Channel;
import com.example.Klein.model.Server;
import com.example.Klein.model.ServerMember;
import com.example.Klein.model.User;
import com.example.Klein.repository.ChannelRepository;
import com.example.Klein.repository.ServerMemberRepository;
import com.example.Klein.repository.ServerRepository;
import com.example.Klein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin
public class ServerController {

    @Autowired private ServerRepository serverRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ChannelRepository channelRepo;
    @Autowired private ServerMemberRepository memberRepo;

    // 1. Tạo Server mới (Đã cập nhật có iconUrl)
    // CHỈ GIỮ LẠI MỘT HÀM NÀY THÔI
    @PostMapping("/create")
    public Server createServer(
            @RequestParam Long ownerId,
            @RequestParam String name,
            @RequestParam(required = false) String iconUrl // Tham số mới
    ) {
        User owner = userRepo.findById(ownerId).orElse(null);
        if (owner != null) {
            // A. Tạo Server
            Server server = new Server();
            server.setName(name);
            server.setOwner(owner);
            server.setIconUrl(iconUrl); // Lưu icon
            Server savedServer = serverRepo.save(server);

            // B. Tự động thêm Owner làm thành viên đầu tiên
            ServerMember member = new ServerMember();
            member.setServer(savedServer);
            member.setUser(owner);
            member.setRole("OWNER");
            memberRepo.save(member);

            // C. Tạo kênh mặc định
            createDefaultChannel(savedServer, "chung", "TEXT");
            createDefaultChannel(savedServer, "General", "VOICE");

            return savedServer;
        }
        return null;
    }

    private void createDefaultChannel(Server server, String name, String type) {
        Channel channel = new Channel();
        channel.setName(name);
        channel.setType(type);
        channel.setServer(server);
        channelRepo.save(channel);
    }

    // 2. Lấy danh sách Server của tôi (Bao gồm cả server mình tạo và được mời)
    @GetMapping("/my/{userId}")
    public List<Server> getMyServers(@PathVariable Long userId) {
        return serverRepo.findAllByUserId(userId);
    }

    // 3. Lấy thông tin chi tiết 1 Server
    @GetMapping("/{serverId}")
    public Server getServerDetail(@PathVariable Long serverId) {
        return serverRepo.findById(serverId).orElse(null);
    }

    // 4. Mời thành viên vào Server
    @PostMapping("/{serverId}/invite")
    public String addMember(@PathVariable Long serverId, @RequestParam Long userId) {
        if (memberRepo.existsByServerIdAndUserId(serverId, userId)) {
            return "Người này đã ở trong nhóm rồi!";
        }

        Server server = serverRepo.findById(serverId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (server != null && user != null) {
            ServerMember member = new ServerMember();
            member.setServer(server);
            member.setUser(user);
            member.setRole("MEMBER");
            memberRepo.save(member);
            return "Đã thêm thành viên thành công!";
        }
        return "Lỗi: Không tìm thấy Server hoặc User";
    }

    // 5. Lấy danh sách thành viên trong Server (Dùng để lọc khi mời bạn)
    @GetMapping("/{serverId}/members")
    public List<Long> getServerMemberIds(@PathVariable Long serverId) {
        List<ServerMember> members = memberRepo.findByServerId(serverId);
        List<Long> userIds = new ArrayList<>();
        for (ServerMember m : members) {
            userIds.add(m.getUser().getId());
        }
        return userIds;
    }
}