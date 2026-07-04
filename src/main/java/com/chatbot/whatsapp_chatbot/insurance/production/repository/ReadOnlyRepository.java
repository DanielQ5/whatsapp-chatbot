package com.chatbot.whatsapp_chatbot.insurance.production.repository;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();
}
