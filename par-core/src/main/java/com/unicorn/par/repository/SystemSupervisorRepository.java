package com.unicorn.par.repository;

import com.unicorn.core.repository.BaseRepository;
import com.unicorn.par.domain.po.SystemSupervisor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SystemSupervisorRepository extends BaseRepository<SystemSupervisor> {

    @Modifying
    @Query("delete from SystemSupervisor a where a.system.objectId = ?1")
    void deleteBySystemId(Long systemId);
}
