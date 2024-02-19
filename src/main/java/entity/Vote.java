package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "vote")
@Setter
@Getter
public class Vote implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "value")
    private Integer value;
    @Basic
    @Column(name = "username")
    private String username;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    public Vote() {
    }

    public Vote(Book book, String username) {
        this.book = book;
        this.username = username;
    }

    public Vote(Book book, Integer value, String username) {
        this.book = book;
        this.value = value;
        this.username = username;
    }
}