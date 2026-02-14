package com.upes.lostfound.controller;

import com.upes.lostfound.model.Item;
import com.upes.lostfound.model.RetrievalRequest;
import com.upes.lostfound.repository.ItemRepository;
import com.upes.lostfound.repository.RetrievalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RetrievalRequestRepository retrievalRequestRepository;
    private final ItemRepository itemRepository;

    // 1️⃣ View all pending requests
    @GetMapping("/requests")
    public List<RetrievalRequest> getPendingRequests() {
        return retrievalRequestRepository.findByStatus("PENDING");
    }

    // 2️⃣ Approve request
    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id) {

        RetrievalRequest request = retrievalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("APPROVED");
        retrievalRequestRepository.save(request);

        Item item = request.getItem();
        item.setResolved(true);
        itemRepository.save(item);

        return "Request approved and item marked as resolved.";
    }

    // 3️⃣ Reject request
    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id) {

        RetrievalRequest request = retrievalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        retrievalRequestRepository.save(request);

        return "Request rejected.";
    }
}
