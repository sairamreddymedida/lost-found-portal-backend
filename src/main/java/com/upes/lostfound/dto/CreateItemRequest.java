package com.upes.lostfound.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateItemRequest {

    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate dateReported;
    private String status; // LOST or FOUND
    private String imageUrl;
}
