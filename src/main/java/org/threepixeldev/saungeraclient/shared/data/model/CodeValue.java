package org.threepixeldev.saungeraclient.shared.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "code_id", nullable = false)
    private Code code;

    @Column(nullable = false)
    private String name;

    private String description;
}
