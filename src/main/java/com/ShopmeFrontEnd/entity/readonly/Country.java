package com.ShopmeFrontEnd.entity.readonly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    @NotBlank
    @NotEmpty
    private String name;

    @Column(nullable = false, length = 5)
    @NotBlank
    @NotEmpty
    private String code;

    @OneToMany(mappedBy = "country")
    private Set<State> states;

    public Country(Integer countryId) {
        this.id = countryId;
    }
}
