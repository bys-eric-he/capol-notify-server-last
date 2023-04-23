package com.capol.notify.manage.domain.model;


import com.capol.notify.manage.domain.EnumStatusType;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.time.LocalDateTime;

public class BaseEntity extends IdentifiedDomainObject {

    /**
     * 记录状态(0-删除 1-正常)
     */
    private Integer status;

    private LocalDateTime createdDatetime;

    private LocalDateTime latestModifiedDatetime;

    protected final void preRemove() {
        this.onRemove();
    }

    /**
     * 实现类在需要的时候覆盖该方法
     */
    protected void onRemove() {
        // ignore
    }

    public void buildBaseInfo() {
        if (this.getId() == null || this.getId() == 0L) {
            this.setId(IdGenerator.generateId());
            this.setStatus(EnumStatusType.NORMAL.getCode());
            this.setCreatedDatetime(LocalDateTime.now());
            this.setLatestModifiedDatetime(LocalDateTime.now());
        } else {
            this.setLatestModifiedDatetime(LocalDateTime.now());
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    private void setCreatedDatetime(LocalDateTime createdDateTime) {
        this.createdDatetime = createdDateTime;
    }

    public LocalDateTime getLastModifiedDatetime() {
        return latestModifiedDatetime;
    }

    private void setLatestModifiedDatetime(LocalDateTime latestModifiedDatetime) {
        this.latestModifiedDatetime = latestModifiedDatetime;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, CustomToStringStyle.MULTILINE_INSTANCE,
                false, false, true, BaseEntity.class);
    }
}