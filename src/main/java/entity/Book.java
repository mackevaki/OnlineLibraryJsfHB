package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@RequiredArgsConstructor
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
//    @Basic
//    @Column(name = "genre_id", nullable = false)
//    private Long genreId;
//    @Basic
//    @Column(name = "author_id", nullable = false)
//    private Long authorId;
    @Basic
    @Column(name = "publish_year", nullable = false)
    private Integer publishDate;
//    @Basic
//    @Column(name = "publisher_id", nullable = false)
//    private Long publisherId;
    @Basic
    @Column(name = "image", nullable = true)
    private byte[] image;
    @Basic
    @Column(name = "descr", nullable = true, length = 5000)
    private String descr;
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
    
    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

//    public Long getGenreId() {
//        return genreId;
//    }
//
//    public void setGenreId(Long genreId) {
//        this.genreId = genreId;
//    }
//
//    public Long getAuthorId() {
//        return authorId;
//    }
//
//    public void setAuthorId(Long authorId) {
//        this.authorId = authorId;
//    }

    public Integer getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Integer publishDate) {
        this.publishDate = publishDate;
    }

//    public Long getPublisherId() {
//        return publisherId;
//    }
//
//    public void setPublisherId(Long publisherId) {
//        this.publisherId = publisherId;
//    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

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
//        if (genreId != null ? !genreId.equals(book.genreId) : book.genreId != null) return false;
//        if (authorId != null ? !authorId.equals(book.authorId) : book.authorId != null) return false;
        if (publishDate != null ? !publishDate.equals(book.publishDate) : book.publishDate != null) return false;
//        if (publisherId != null ? !publisherId.equals(book.publisherId) : book.publisherId != null) return false;
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
//        result = 31 * result + (genreId != null ? genreId.hashCode() : 0);
//        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
//        result = 31 * result + (publisherId != null ? publisherId.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(image);
        result = 31 * result + (descr != null ? descr.hashCode() : 0);
        return result;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
