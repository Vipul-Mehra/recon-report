package com.entity;

import java.io.Serializable;
import java.util.Objects;

public class CompositeKey implements Serializable {
    private Long etrmDealId;
    private Integer etrmUniqueDealLegId;
    private Integer legNumberDisplay;

    public CompositeKey() {}

    public CompositeKey(Long etrmDealId, Integer etrmUniqueDealLegId, Integer legNumberDisplay) {
        this.etrmDealId = etrmDealId;
        this.etrmUniqueDealLegId = etrmUniqueDealLegId;
        this.legNumberDisplay = legNumberDisplay;
    }

    public Long getEtrmDealId() {
        return etrmDealId;
    }

    public void setEtrmDealId(Long etrmDealId) {
        this.etrmDealId = etrmDealId;
    }

    public Integer getEtrmUniqueDealLegId() {
        return etrmUniqueDealLegId;
    }

    public void setEtrmUniqueDealLegId(Integer etrmUniqueDealLegId) {
        this.etrmUniqueDealLegId = etrmUniqueDealLegId;
    }

    public Integer getLegNumberDisplay() {
        return legNumberDisplay;
    }

    public void setLegNumberDisplay(Integer legNumberDisplay) {
        this.legNumberDisplay = legNumberDisplay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeKey)) return false;
        CompositeKey that = (CompositeKey) o;
        return Objects.equals(etrmDealId, that.etrmDealId) &&
                Objects.equals(etrmUniqueDealLegId, that.etrmUniqueDealLegId) &&
                Objects.equals(legNumberDisplay, that.legNumberDisplay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(etrmDealId, etrmUniqueDealLegId, legNumberDisplay);
    }

    @Override
    public String toString() {
        return "CompositeKey{" +
                "etrmDealId=" + etrmDealId +
                ", etrmUniqueDealLegId=" + etrmUniqueDealLegId +
                ", legNumberDisplay=" + legNumberDisplay +
                '}';
    }
}