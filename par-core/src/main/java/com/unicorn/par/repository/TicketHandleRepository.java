package com.unicorn.par.repository;

import com.unicorn.core.repository.BaseRepository;
import com.unicorn.par.domain.po.TicketHandle;
import org.springframework.data.jpa.repository.Query;

public interface TicketHandleRepository extends BaseRepository<TicketHandle> {

    @Query("select a from TicketHandle a where a.ticket.objectId = ?1")
    TicketHandle findByTicketId(Long objectId);
}
