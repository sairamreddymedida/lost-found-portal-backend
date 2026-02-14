package com.upes.lostfound.repository;

import com.upes.lostfound.model.Item;
import com.upes.lostfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCreatedBy(User user);

    List<Item> findByIsResolvedFalse();
}
