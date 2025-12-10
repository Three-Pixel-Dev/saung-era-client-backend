package org.threepixeldev.saungeraclient.shared.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "profile_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    @MapsId
    private User user;

    @Column(name = "country_id")
    private Long countryId;

    private String address;

    private String kyc;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender_id")
    private Long genderId;

    private Integer points;

    @Column(name = "referral_code")
    private String referralCode;
}
