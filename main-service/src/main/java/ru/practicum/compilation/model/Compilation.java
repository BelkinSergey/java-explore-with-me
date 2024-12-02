package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "events_id"))
    @ToString.Exclude
    private List<Event> events;


}
