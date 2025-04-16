package com.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ELEOX_OXNOM_TRADE_EXTRACT")
@IdClass(CompositeKey.class)
public class EleoxOxNomTradeExtract {

    @Id
    @Column(name = "ETRMDEALID", nullable = false)
    private Long etrmDealId;

    @Column(name = "PROCESS_TYPE")
    private String processType;

    @Column(name = "OUTCOME")
    private String outcome;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "TRADEDATE", insertable = false, updatable = false)
    private String tradeDateString;

    @Column(name = "TRDSTARTDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date trdStartDate;

    @Column(name = "TRDENDDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date trdEndDate;

    @Column(name = "VOLUME", precision = 19, scale = 2)
    private BigDecimal volume;

    @Column(name = "PRICEDISPLAY", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceDisplay;

    @Column(name = "RANKDISPLAY")
    private String rankDisplay;

    @Column(name = "FLOWDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date flowDate;

    @Column(name = "LEGDAILYELECTIONVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal legalDailyElectionVolume;

    @Column(name = "LEGESTIMATEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal legEstimatedVolume;

    @Column(name = "LEGNOMINATEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal legNominatedVolume;

    @Column(name = "LEGSCHEDULEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal legScheduledVolume;

    @Column(name = "LEGTRADINGVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal legTradingVolume;

    @Column(name = "ETRMSCHEDULEDLOCATIONID", nullable = false)
    private String etrmScheduledLocationId;

    @Column(name = "ESTIMATEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal estimatedVolume;

    @Column(name = "NOMINATEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal nominatedVolume;

    @Column(name = "SCHEDULEDVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal scheduledVolume;

    @Column(name = "TRADINGVOLUME", nullable = false, precision = 19, scale = 2)
    private BigDecimal tradingVolume;

    @Column(name = "INTERNAL_COMPANY", nullable = false)
    private String internalCompany;

    @Column(name = "STRATEGY", nullable = false)
    private String strategy;

    @Column(name = "TRADER", nullable = false)
    private String trader;

    @Column(name = "COUNTERPARTY", nullable = false)
    private String counterParty;

    @Column(name = "COUNTERPARTY_TYPE", nullable = false)
    private String counterPartyType;

    @Column(name = "PRICETYPE", nullable = false)
    private String priceType;

    @Column(name = "MTMCURVE", nullable = false)
    private String mtmCurve;

    @Column(name = "FORMULA")
    private String formula;

    @Column(name = "CU_CURRENCY", nullable = false)
    private String cuCurrency;

    @Column(name = "UT_UNIT", nullable = false)
    private String utUnit;

    @Column(name = "PIPELINE", nullable = false)
    private String pipeline;

    @Column(name = "PZ_ZONE", nullable = false)
    private String pzZone;

    @Column(name = "BASIS", nullable = false)
    private Integer basis;

    @Column(name = "ADDER", nullable = false, precision = 19, scale = 2)
    private BigDecimal adder;

    @Column(name = "FUEL", nullable = false, precision = 19, scale = 2)
    private BigDecimal fuel;

    @Column(name = "EST_FIXED_PRICE", precision = 19, scale = 2)
    private BigDecimal estFixedPrice;

    @Column(name = "AUTO_IC")
    private String autoIc;

    @Column(name = "AMA")
    private String ama;

    @Column(name = "GROUPID")
    private String groupId;

    @Column(name = "MODIFY_USER", nullable = false)
    private String modifyUser;

    @Column(name = "MODIFY_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

    @Id
    @Column(name = "ETRMUNIQUEDEALLEGID", nullable = false)
    private Integer etrmUniqueDealLegId;

    @Id
    @Column(name = "LEGNUMBERDISPLAY", nullable = false)
    private Integer legNumberDisplay;

    @Column(name = "ETRMLOCATIONID", nullable = false)
    private String etrmLocationId;

    @Column(name = "ETRMPIPELINEID", nullable = false)
    private String etrmPipelineId;

    @Column(name = "LEGSTARTDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date legStartDate;

    @Column(name = "LEGENDDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date legendDate;

    @Column(name = "DEALTYPEID")
    private String dealTypeId;

    @Column(name = "ETRMVERSIONNUM")
    private Integer etrmVersionNum;

    @Column(name = "DEALTRANSTATUS")
    private String dealTranStatus;

    @Column(name = "BUYSELL", nullable = false)
    private String buySell;

    @Column(name = "ETRMINTERNALPARTYID", nullable = false)
    private Integer etrmInternalPartyId;

    @Column(name = "ETRMEXTERNALPARTYID", nullable = false)
    private Integer etrmExternalPartyId;

    @Column(name = "TRADERDISPLAY")
    private String traderDisplay;

    public Long getEtrmDealId() {
        return etrmDealId;
    }

    public void setEtrmDealId(Long etrmDealId) {
        this.etrmDealId = etrmDealId;
    }
}