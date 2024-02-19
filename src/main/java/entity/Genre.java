package entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import java.io.Serializable;
import java.util.Collection;

@Entity(name = "genre")
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "name")
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class Genre implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "genre")
    private Collection<Book> books;
}
