package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity(name = "book")
//@NoArgsConstructor
//@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@DynamicUpdate // обновляет только те поля, которые изменились
@DynamicInsert // вставляет только те поля, у которых есть значение
@SelectBeforeUpdate // проверить объект перед обновлением, нужно ли его обновлять
@EqualsAndHashCode(of = "id")
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
    private Integer avgRating = 0;
    @Basic
    @Column(name = "total_vote_count")
    private Long totalVoteCount = 0L;
    @Basic
    @Column(name = "total_rating")
    private Long totalRating = 0L;
    @Basic
    @Column(name = "view_count")
    private Long viewCount = 0L;
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
}
