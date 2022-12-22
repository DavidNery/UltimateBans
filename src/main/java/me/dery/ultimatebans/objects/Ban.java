package me.dery.ultimatebans.objects;

public class Ban {

    private final int id;
    private final long until, when;
    private final String reason, appliedBy;

    private boolean active;

    private String unbannedBy;

    public Ban(int id, long until, long when, String reason, String appliedBy) {
        this(id, until, when, reason, appliedBy, true, null);
    }

    public Ban(int id, long until, long when, String reason, String appliedBy, boolean active, String unbannedBy) {
        this.id = id;
        this.until = until;
        this.when = when;
        this.reason = reason;
        this.appliedBy = appliedBy;
        this.active = active;
        this.unbannedBy = unbannedBy;
    }

    public int getId() {
        return id;
    }

    public long getUntil() {
        return until;
    }

    public long getWhen() {
        return when;
    }

    public String getReason() {
        return reason;
    }

    public String getAppliedBy() {
        return appliedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUnbannedBy() {
        return unbannedBy;
    }

    public void setUnbannedBy(String unbannedBy) {
        this.unbannedBy = unbannedBy;
    }

    public boolean isPermanent() {
        return until == -1L;
    }
}
