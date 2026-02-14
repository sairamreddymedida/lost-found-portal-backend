package com.upes.lostfound.repository;

import com.upes.lostfound.model.RetrievalRequest;
import com.upes.lostfound.model.User;
import com.upes.lostfound.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetrievalRequestRepository extends JpaRepository<RetrievalRequest, Long> {

    List<RetrievalRequest> findByStatus(String status);

    List<RetrievalRequest> findByRequestedBy(User user);

    boolean existsByItemAndRequestedBy(Item item, User user);
}
