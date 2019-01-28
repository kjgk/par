package com.unicorn.par.repository;

import com.unicorn.core.repository.BaseRepository;
import com.unicorn.par.domain.po.Accendant;

public interface AccendantRepository extends BaseRepository<Accendant> {

    Accendant findByUserObjectId(Long userId);
}
