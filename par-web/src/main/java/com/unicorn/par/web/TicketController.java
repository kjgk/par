package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.po.User;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.domain.po.QTicket;
import com.unicorn.par.domain.po.Ticket;
import com.unicorn.par.domain.po.TicketHandle;
import com.unicorn.par.domain.vo.TicketInfo;
import com.unicorn.par.service.AccendantService;
import com.unicorn.par.service.TicketService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccendantService accendantService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<TicketInfo> list(PageInfo pageInfo, String keyword) {

        QTicket ticket = QTicket.ticket;

        BooleanExpression expression = ticket.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(
                        ticket.content.containsIgnoreCase(s)
                                .or(ticket.contacts.containsIgnoreCase(s))
                                .or(ticket.phoneNo.containsIgnoreCase(s))
                );
            }
        }

        User currentUser = userService.getCurrentUser();
        String roleTag = currentUser.getUserRoleList().get(0).getRole().getTag();
        if ("Accendant".equals(roleTag)) {
            Accendant currentAccendant = accendantService.getCurrentAccendant();
            expression = expression.and(ticket.system.company.objectId.eq(currentAccendant.getCompany().getObjectId()));
        }
        if ("Supervisor".equals(roleTag)) {
            expression = expression.and(ticket.submitter.objectId.eq(currentUser.getObjectId()));
        }

        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "createdDate")
        );
        return ticketService.getTicketInfo(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return ticketService.getTicket();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createTicket(@RequestBody Ticket ticket) {

        ticketService.saveTicket(ticket);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteTicket(@PathVariable("objectId") Long objectId) {

        ticketService.deleteTicket(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        ticketService.deleteTicket(objectIds);
    }

    @RequestMapping(value = "/{objectId}/accept", method = RequestMethod.POST)
    public void acceptTicket(@PathVariable("objectId") Long objectId) {

        ticketService.acceptTicket(objectId);
    }

    @RequestMapping(value = "/{objectId}/process", method = RequestMethod.POST)
    public void processTicket(@PathVariable("objectId") Long objectId, @RequestBody TicketHandle ticketHandle) {

        ticketService.processTicket(objectId, ticketHandle);
    }

    @RequestMapping(value = "/record", method = RequestMethod.POST)
    public void recordTicket(@RequestBody TicketHandle ticketHandle) {

        ticketService.recordTicket(ticketHandle.getTicket(), ticketHandle);
    }
}
