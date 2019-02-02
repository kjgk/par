package com.unicorn.par.web;

import com.unicorn.core.domain.DefaultRecursive;
import com.unicorn.system.domain.po.Menu;
import com.unicorn.system.domain.po.User;
import com.unicorn.system.service.MenuService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1)
public class MainController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Map current() {

        Map result = new HashMap();
        Map user = new HashMap();
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            user.put("id", currentUser.getObjectId());
            user.put("username", currentUser.getName());
            user.put("role", currentUser.getUserRoleList().get(0).getRole().getTag());
            result.put("user", user);
        }
        result.put("success", true);
        return result;
    }

    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    public List<Map> menus() {

        List<Map> result = new ArrayList();
        List<Long> menus = userService.getUserMenus(userService.getCurrentUser().getObjectId());
        List<Menu> all = menuService.getAll();
        all.sort(Comparator.comparingInt(DefaultRecursive::getOrderNo));

        for (Menu menu : all) {
            if (menu.getEnabled() == 0 || menu.getParent() == null || !menus.contains(menu.getObjectId())) {
                continue;
            }
            Map item = new HashMap();
            item.put("id", menu.getObjectId());
            item.put("name", menu.getName());
            item.put("route", menu.getUrl());
            item.put("icon", menu.getIcon());
            item.put("hidden", menu.getHidden());
            if (menu.getParent() != null && menu.getParent().getParent() != null) {
                item.put("bpid", menu.getParent().getObjectId());
                item.put("mpid", menu.getHidden() == 0 ? menu.getParent().getObjectId() : "-1");
            }
            result.add(item);
        }
        return result;
    }
}
