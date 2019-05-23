package com.unicorn.par.service;

import com.unicorn.core.domain.po.Account;
import com.unicorn.core.domain.po.Role;
import com.unicorn.core.domain.po.User;
import com.unicorn.core.domain.po.UserRole;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.core.repository.RoleRepository;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.repository.AccendantRepository;
import com.unicorn.system.service.AccountService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AccendantService {

    @Autowired
    private AccendantRepository accendantRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    public Accendant getCurrentAccendant() {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return getByUser(currentUser);
    }

    public Accendant getByUser(User user) {

        return accendantRepository.findByUserObjectId(user.getObjectId());
    }

    public Page<Accendant> getAccendant(QueryInfo queryInfo) {

        return accendantRepository.findAll(queryInfo);
    }

    public Accendant getAccendant(Long objectId) {

        return accendantRepository.get(objectId);
    }

    public void saveAccendant(Accendant accendant) {

        Accendant current;
        if (StringUtils.isEmpty(accendant.getObjectId())) {

            String phoneNo = accendant.getPhoneNo();

            Role role = roleRepository.findByTag("Accendant");
            UserRole userRole = new UserRole();
            userRole.setRole(role);

            // 保存到User表
            User user = new User();
            user.setName(accendant.getUsername());
            user.setUserRoleList(Arrays.asList(userRole));
            user = userService.saveUser(user);

            // 保存到帐号表
            Account account = new Account();
            account.setPassword("111111");
            account.setUser(user);
            account.setName(phoneNo);
            accountService.saveAccount(account);

            // 保存到Accendant表
            accendant.setUser(user);
            current = accendantRepository.save(accendant);
        } else {
            current = accendantRepository.getOne(accendant.getObjectId());
            current.getUser().setName(accendant.getUsername());
            current.setCompany(accendant.getCompany());
            current.setPhoneNo(accendant.getPhoneNo());
        }
    }

    public void deleteAccendant(Long objectId) {

        accendantRepository.deleteById(objectId);
    }

    public void deleteAccendant(List<Long> objectIds) {

        objectIds.forEach(this::deleteAccendant);
    }
}
