package com.unicorn.par.service;

import com.unicorn.core.domain.po.Account;
import com.unicorn.core.domain.po.Role;
import com.unicorn.core.domain.po.User;
import com.unicorn.core.domain.po.UserRole;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.core.repository.RoleRepository;
import com.unicorn.par.domain.po.Supervisor;
import com.unicorn.par.repository.SupervisorRepository;
import com.unicorn.system.service.AccountService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupervisorService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    public Supervisor getCurrentSupervisor() {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return getByUser(currentUser);
    }

    public Supervisor getByUser(User user) {

        return supervisorRepository.findByUserObjectId(user.getObjectId());
    }

    public Page<Supervisor> getSupervisor(QueryInfo queryInfo) {

        return supervisorRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getSupervisor() {

        return supervisorRepository.findAll().stream().map(supervisor -> BasicInfo.valueOf(supervisor.getObjectId(), supervisor.getUsername())).collect(Collectors.toList());
    }


    public Supervisor getSupervisor(Long objectId) {

        return supervisorRepository.get(objectId);
    }

    public void saveSupervisor(Supervisor supervisor) {

        Supervisor current;
        if (StringUtils.isEmpty(supervisor.getObjectId())) {

            String phoneNo = supervisor.getPhoneNo();

            Role role = roleRepository.findByTag("Supervisor");
            UserRole userRole = new UserRole();
            userRole.setRole(role);

            // 保存到User表
            User user = new User();
            user.setName(supervisor.getUsername());
            user.setUserRoleList(Arrays.asList(userRole));
            user = userService.saveUser(user);

            // 保存到帐号表
            Account account = new Account();
            account.setPassword("111111");
            account.setUser(user);
            account.setName(phoneNo);
            accountService.saveAccount(account);

            // 保存到Supervisor表
            supervisor.setUser(user);
            current = supervisorRepository.save(supervisor);
        } else {
            current = supervisorRepository.getOne(supervisor.getObjectId());
            current.getUser().setName(supervisor.getUsername());
            current.setPhoneNo(supervisor.getPhoneNo());
        }
    }

    public void deleteSupervisor(Long objectId) {

        supervisorRepository.deleteById(objectId);
    }

    public void deleteSupervisor(List<Long> objectIds) {

        objectIds.forEach(this::deleteSupervisor);
    }
}
