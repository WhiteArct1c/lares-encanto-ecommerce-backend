package com.laresencanto.laresencantorestapi.domain;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "gender")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
