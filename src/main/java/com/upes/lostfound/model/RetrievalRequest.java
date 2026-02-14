package com.upes.lostfound.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "retrieval_requests")
public class RetrievalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    private String status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt;
}
