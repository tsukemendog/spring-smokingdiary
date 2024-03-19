package io.github.tsukemendog.nondesire.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChallengeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long levelNumber;

    private Long duration;

    private String reward;

    private String image;

    private String title;

    private String message;

    @OneToMany(mappedBy = "challengeLevel")
    private List<Progress> progresses = new ArrayList<>();

}
