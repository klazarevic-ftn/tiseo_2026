package com.fin.tiseo.scheduling_service.service;

import com.fin.tiseo.scheduling_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repository;
}