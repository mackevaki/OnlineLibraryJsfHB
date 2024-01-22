package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "book")
//@NoArgsConstructor
//@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class Book implements Serializable{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "content", nullable = true)
    private byte[] content;
    @Basic
    @Column(name = "page_count", nullable = false)
    private Integer pageCount;
    @Basic
    @Column(name = "isbn", nullable = false, length = 100)
    private String isbn;
    @Basic
    @Column(name = "publish_year", nullable = false)
    private Integer publishDate;
    @Basic
    @Column(name = "image", nullable = true)
    private byte[] image;
    @Basic
    @Column(name = "descr", nullable = true, length = 5000)
    private String descr;
    @Basic
    @Column(name = "avg_rating")
    private Integer avgRating;
    @Basic
    @Column(name = "total_vote_count")
    private Long totalVoteCount;
    @Basic
    @Column(name = "total_rating")
    private Long totalRating;
    @Basic
    @Column(name = "view_count")
    private Long viewCount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", referencedColumnName = "id", nullable = false)
    private Genre genre;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private Author author;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", referencedColumnName = "id", nullable = false)
    private Publisher publisher;
    private boolean edit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        if (name != null ? !name.equals(book.name) : book.name != null) return false;
        if (!Arrays.equals(content, book.content)) return false;
        if (pageCount != null ? !pageCount.equals(book.pageCount) : book.pageCount != null) return false;
        if (isbn != null ? !isbn.equals(book.isbn) : book.isbn != null) return false;
        if (publishDate != null ? !publishDate.equals(book.publishDate) : book.publishDate != null) return false;
        if (!Arrays.equals(image, book.image)) return false;
        if (descr != null ? !descr.equals(book.descr) : book.descr != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(content);
        result = 31 * result + (pageCount != null ? pageCount.hashCode() : 0);
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(image);
        result = 31 * result + (descr != null ? descr.hashCode() : 0);
        return result;
    }
}
