package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import lombok.*;

@Entity(name = "publisher")
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "name")
public class Publisher implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "publisher")
    private Collection<Book> books;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Publisher publisher = (Publisher) o;
//
//        if (id != null ? !id.equals(publisher.id) : publisher.id != null) return false;
//        if (name != null ? !name.equals(publisher.name) : publisher.name != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (name != null ? name.hashCode() : 0);
//        return result;
//    }
//
//    public Collection<Book> getBooks() {
//        return books;
//    }
//
//    public void setBooks(Collection<Book> books) {
//        this.books = books;
//    }
}
