package com.fin.tiseo.scheduling_service.repository;

import com.fin.tiseo.scheduling_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}