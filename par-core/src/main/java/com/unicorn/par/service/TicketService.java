package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.enumeration.TicketStatus;
import com.unicorn.par.domain.po.Ticket;
import com.unicorn.par.domain.po.TicketHandle;
import com.unicorn.par.repository.TicketHandleRepository;
import com.unicorn.par.repository.TicketRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.domain.vo.FileUploadInfo;
import com.unicorn.std.service.ContentAttachmentService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHandleRepository ticketHandleRepository;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private UserService userService;

    public Page<Ticket> getTicket(QueryInfo queryInfo) {

        return ticketRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getTicket() {

        return ticketRepository.list();
    }

    public Ticket getTicket(Long objectId) {

        return ticketRepository.get(objectId);
    }

    public void saveTicket(Ticket ticket) {

        Ticket current;
        if (StringUtils.isEmpty(ticket.getObjectId())) {
            current = ticketRepository.save(ticket);
            current.setSubmitTime(new Date());
            current.setSubmitter(userService.getCurrentUser());
            current.setStatus(TicketStatus.pending);
            if (!CollectionUtils.isEmpty(ticket.getAttachments())) {
                List<ContentAttachment> contentAttachments = new ArrayList();
                for (FileUploadInfo fileUploadInfo : ticket.getAttachments()) {
                    ContentAttachment contentAttachment = new ContentAttachment();
                    contentAttachment.setFileInfo(fileUploadInfo);
                    contentAttachment.setRelatedType(Ticket.class.getSimpleName());
                    contentAttachment.setRelatedId(current.getObjectId());
                    contentAttachments.add(contentAttachment);
                }
                contentAttachmentService.save(Ticket.class.getSimpleName(), current.getObjectId(), null, contentAttachments);
            }
        } else {
        }
    }

    public void acceptTicket(Long objectId) {

        Ticket ticket = getTicket(objectId);
        ticket.setStatus(TicketStatus.processing);

        TicketHandle ticketHandle = new TicketHandle();
        ticketHandle.setAccendant(accendantService.getCurrentAccendant());
        ticketHandle.setAcceptTime(new Date());
        ticketHandle.setTicket(ticket);
        ticketHandleRepository.save(ticketHandle);
    }

    public void deleteTicket(Long objectId) {

        ticketRepository.deleteById(objectId);
    }

    public void deleteTicket(List<Long> objectIds) {

        objectIds.forEach(this::deleteTicket);
    }
}
