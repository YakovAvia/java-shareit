package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    List<Item> findAllByRequest_Id(Long requestId);


    @Query(value = "SELECT i FROM Item as i WHERE " +
            "(LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(concat('%', :text,'%'))) AND " +
            "i.available = true ")
    List<Item> searchItem(String text);


    List<Item> findAllByUser_Id(Long userId);

    List<Item> findAllByRequest_IdIn(List<Long> requestIds);
}
