package com.unicorn.par.domain.vo;

import com.unicorn.std.domain.vo.FileDownloadInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TicketInfo implements Serializable {

    private Long objectId;

    private Integer priority;

    private String content;

    private String systemName;

    private Long systemId;

    private String contacts;

    private String phoneNo;

    private String submitter;

    private Date submitTime;

    private Integer status;

    private List<FileDownloadInfo> attachments;

    private TicketHandleInfo handleInfo;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketHandleInfo implements Serializable {

        private Long objectId;

        private String accendant;

        private Date acceptTime;

        private Date finishTime;

        private Integer result;

        private String remark;

        private List<FileDownloadInfo> attachments;
    }
}


