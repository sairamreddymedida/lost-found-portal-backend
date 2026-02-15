package com.upes.lostfound.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upes.lostfound.dto.CreateItemRequest;
import com.upes.lostfound.model.Item;
import com.upes.lostfound.model.RetrievalRequest;
import com.upes.lostfound.model.User;
import com.upes.lostfound.repository.ItemRepository;
import com.upes.lostfound.repository.RetrievalRequestRepository;
import com.upes.lostfound.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RetrievalRequestRepository retrievalRequestRepository;

    @GetMapping("/public")
    public Object getAllActiveItems() {
        return itemRepository.findByIsResolvedFalse();
    }

    @GetMapping("/my")
    public Object getMyItems() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return itemRepository.findByCreatedBy(user);
    }

    @PostMapping("/{itemId}/retrieve")
    public String requestRetrieval(@PathVariable Long itemId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.isResolved()) {
            return "Item already resolved!";
        }

        if (retrievalRequestRepository.existsByItemAndRequestedBy(item, user)) {
            return "You have already requested retrieval for this item.";
        }

        RetrievalRequest request = new RetrievalRequest();
        request.setItem(item);
        request.setRequestedBy(user);
        request.setStatus("PENDING");
        request.setCreatedAt(java.time.LocalDateTime.now());

        retrievalRequestRepository.save(request);

        return "Retrieval request submitted successfully!";
    }

    @PostMapping
    public String createItem(@RequestBody CreateItemRequest request) {

        // Get logged-in user email from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Item item = new Item();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setDateReported(request.getDateReported());
        item.setStatus(request.getStatus());
        item.setImageUrl(request.getImageUrl());
        item.setResolved(false);
        item.setCreatedBy(user);
        item.setCreatedAt(LocalDateTime.now());
        if (item.getCreatedBy().getId().equals(user.getId())) {
            return "You cannot request retrieval for your own item.";
        }

        itemRepository.save(item);

        return "Item created successfully!";
    }
}
