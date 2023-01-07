package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

@Getter
@Setter
@Builder
public class ItemRequestDto {

  private Long id;
  private String description;
  private LocalDateTime created;
  private List<ItemDto> items;
}
