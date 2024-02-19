package entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

@Entity(name = "author")
@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = "fio")
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class Author implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "fio", nullable = false, length = 300)
    private String fio;
    @Basic
    @Column(name = "birthday", nullable = false)
    private Date birthday;
    @OneToMany(mappedBy = "author")
    private Collection<Book> books;
}
