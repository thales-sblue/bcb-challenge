package com.thales.bcb.modules.message.repository;


import com.thales.bcb.modules.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationId(UUID conversationId);

    List<Message> findBySenderId(UUID senderId);

    List<Message> findByRecipientId(UUID recipientId);
}

