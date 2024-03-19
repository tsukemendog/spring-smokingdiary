package io.github.tsukemendog.nondesire.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "Members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceId;

    //private String name;

    //private String profileImage;

    private String provider;

    private LocalDateTime regDate;

    private LocalDateTime startTime;

    private Boolean isActive;

    @OneToMany(mappedBy = "member")
    private List<Daily> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Progress> progresses = new ArrayList<>();

}
