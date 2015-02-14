package finalproject.shareboard.model;

import java.util.Date;

import finalproject.shareboard.framework.Globals;

public class Ad {
    private Integer AdId;
    private Integer BoardId;
    private Integer UserId;
    private Date createTime;
    private Globals.adType AdType;
    private String AdTitle;
    private Globals.adPriority AdPriority;
    private String AdDesc;
    private Date fromTime;
    private Date toTime;
    private Date lastUpdate;
    private Integer lastUpdateBy;

    public Integer getAdId() {
        return AdId;
    }

    public void setAdId(Integer adId) {
        AdId = adId;
    }

    public Integer getBoardId() {
        return BoardId;
    }

    public void setBoardId(Integer boardId) {
        BoardId = boardId;
    }

    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer userId) {
        UserId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Globals.adType getAdType() {
        return AdType;
    }

    public void setAdType(Globals.adType adType) {
        AdType = adType;
    }

    public String getAdTitle() {
        return AdTitle;
    }

    public void setAdTitle(String adTitle) {
        AdTitle = adTitle;
    }

    public Globals.adPriority getAdPriority() {
        return AdPriority;
    }

    public void setAdPriority(Globals.adPriority adPriority) {
        AdPriority = adPriority;
    }

    public String getAdDesc() {
        return AdDesc;
    }

    public void setAdDesc(String adDesc) {
        AdDesc = adDesc;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(Integer lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Ad(Integer adId, Integer boardId, Integer userId, Globals.adType adType, String adTitle, Globals.adPriority adPriority, String adDesc, Date createTime, Date fromTime, Date toTime, Date lastUpdate, Integer lastUpdateBy) {
        AdId = adId;
        BoardId = boardId;
        UserId = userId;
        AdType = adType;
        AdTitle = adTitle;
        AdPriority = adPriority;
        AdDesc = adDesc;
        this.createTime = createTime;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
}
