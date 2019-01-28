package com.unicorn.par.repository;

import com.unicorn.core.repository.BaseRepository;
import com.unicorn.par.domain.po.SignUser;

public interface SignUserRepository extends BaseRepository<SignUser> {

    SignUser findByUserObjectId(Long userId);
}
