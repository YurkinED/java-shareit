package ru.practicum.shareit.request.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

  List<ItemRequest> findAllByRequesterId(long userId, Sort sort);

  List<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);
}
