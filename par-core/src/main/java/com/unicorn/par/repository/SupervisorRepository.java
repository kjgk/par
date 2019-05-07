package com.unicorn.par.repository;

import com.unicorn.core.repository.BaseRepository;
import com.unicorn.par.domain.po.Supervisor;

public interface SupervisorRepository extends BaseRepository<Supervisor> {

    Supervisor findByUserObjectId(Long userId);
}
