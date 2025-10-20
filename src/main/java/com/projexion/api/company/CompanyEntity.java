package com.projexion.api.company;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompanyEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "CompaniesSeq",
            sequenceName = "companies_id_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @GeneratedValue(generator = "CompaniesSeq")
    private Long id;

    @Column(name = "company_name", columnDefinition = "TEXT")
    private String companyName;

    @Column(name = "company_mail", length = 255)
    private String companyMail;

    @Column(name = "company_phone", length = 255)
    private String companyPhone;

    @Column(name = "company_phone2", columnDefinition = "TEXT")
    private String companyPhone2;

    @Column(name = "company_phone3", length = 255)
    private String companyPhone3;

    @Column(name = "company_phone4", length = 255)
    private String companyPhone4;

    @Column(name = "company_phone5", length = 255)
    private String companyPhone5;

    @Column(name = "company_street", length = 255)
    private String companyStreet;

    @Column(name = "company_city", length = 255)
    private String companyCity;

    @Column(name = "company_postcode", length = 255)
    private String companyPostcode;

    @Column(name = "company_state", length = 255)
    private String companyState;

    @Column(name = "company_postbox", length = 255)
    private String companyPostbox;

    @Column(name = "company_country", length = 255)
    private String companyCountry;

    @Column(name = "company_website", length = 255)
    private String companyWebsite;

    @Column(name = "company_facebook", length = 255)
    private String companyFacebook;

    @Column(name = "company_fax", length = 255)
    private String companyFax;

    @Column(name = "company_durchwahl", length = 255)
    private String companyDurchwahl;

    @Column(name = "company_instagram", length = 255)
    private String companyInstagram;

    @Column(name = "company_twitter", length = 255)
    private String companyTwitter;

    @Column(name = "firmenadresscat", columnDefinition = "JSONB")
    private String firmenAdressCat;

    @Column(name = "company_notes", columnDefinition = "TEXT")
    private String companyNotes;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}