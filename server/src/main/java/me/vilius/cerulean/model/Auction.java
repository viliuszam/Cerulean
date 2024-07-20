package me.vilius.cerulean.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Double startingBid;

    @Column(nullable = false)
    private Double currentBid;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "auction")
    private List<Bid> bids;

    @Column
    private Double reservePrice;

    @Column
    private Double buyItNowPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @ElementCollection
    @CollectionTable(name = "auction_images", joinColumns = @JoinColumn(name = "auction_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    public Auction() {
        this.imageUrls = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(Double startingBid) {
        this.startingBid = startingBid;
    }

    public Double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(Double currentBid) {
        this.currentBid = currentBid;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

    public Double getBuyItNowPrice() {
        return buyItNowPrice;
    }

    public void setBuyItNowPrice(Double buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
