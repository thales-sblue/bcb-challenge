package com.thales.bcb.modules.message.repository;


import com.thales.bcb.modules.message.dto.MessageDTO;
import com.thales.bcb.modules.message.entity.Message;
import com.thales.bcb.modules.message.enums.Priority;
import com.thales.bcb.modules.message.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByConversationId(UUID conversationId);

    List<Message> findByPriorityAndStatus(Priority priority, Status status);

    List<Message> findByPriority(Priority priority);

    List<Message> findByStatus(Status status);

    @Query("""
    SELECT m.id FROM Message m
    WHERE m.conversationId = :conversationId
    AND m.recipientId = :recipientId
    AND m.status = :status
    """)
    List<UUID> findMessageIdsByConversationIdAndRecipientIdAndStatus(
            @Param("conversationId") UUID conversationId,
            @Param("recipientId") UUID recipientId,
            @Param("status") Status status
    );

    int countByConversationIdAndStatus(UUID conversationId, Status status);

}

