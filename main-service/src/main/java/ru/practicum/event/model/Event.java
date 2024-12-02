package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_annotation")
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "event_category_id")
    private Category category;

    @Column(name = "event_created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "event_initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "event_location_id")
    private Location location;

    @Column(name = "event_paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "event_published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    private State state;

    @Column(name = "event_title")
    private String title;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @Transient
    private Long views;
}
