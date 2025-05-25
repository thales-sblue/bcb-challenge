package com.thales.bcb.modules.conversation.repository;

import com.thales.bcb.modules.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
           SELECT c FROM Conversation c
            WHERE
            (c.clientId = :clientId OR c.recipientId = :clientId)
           """)
    List<Conversation> findByClientId(@Param("clientId") UUID clientId);

    @Query("""
           SELECT c FROM Conversation c
            WHERE
            (c.clientId = :clientId AND c.recipientId = :recipientId)
            OR
            (c.clientId = :recipientId AND c.recipientId = :clientId)
           """)
    Optional<Conversation> findByParticipants(
            @Param("clientId") UUID clientId,
            @Param("recipientId") UUID recipientId
    );
}
