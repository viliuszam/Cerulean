package me.vilius.cerulean.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_ratings")
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating; // e.g., 1 to 5 stars

    @Column(nullable = false)
    private String review;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Auction transaction;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Auction getTransaction() {
        return transaction;
    }

    public void setTransaction(Auction transaction) {
        this.transaction = transaction;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

}
