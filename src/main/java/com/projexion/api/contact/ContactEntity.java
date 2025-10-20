package com.projexion.api.contact;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "contacts")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ContactEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "ContactsSeq",
            sequenceName = "contacts_id_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @GeneratedValue(generator = "ContactsSeq")
    private Long id;

    @Column(name = "ahv_number", columnDefinition = "TEXT")
    private String ahvNumber;

    @Column(name = "nationality", columnDefinition = "TEXT")
    private String nationality;

    @Column(name = "withprefix", columnDefinition = "TEXT")
    private String withPrefix;

    @Column(name = "titels", columnDefinition = "TEXT")
    private String titels;

    @Column(name = "prefix", columnDefinition = "TEXT")
    private String prefix;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "firstname", columnDefinition = "TEXT")
    private String firstName;

    @Column(name = "private_address_street", columnDefinition = "TEXT")
    private String privateAddressStreet;

    @Column(name = "private_address_postcode", columnDefinition = "TEXT")
    private String privateAddressPostcode;

    @Column(name = "private_address_city", columnDefinition = "TEXT")
    private String privateAddressCity;

    @Column(name = "private_address_country", columnDefinition = "TEXT")
    private String privateAddressCountry;

    @Column(name = "companyposition", columnDefinition = "TEXT")
    private String companyPosition;

    @Column(name = "postcheck_account", columnDefinition = "TEXT")
    private String postcheckAccount;

    @Column(name = "bank", columnDefinition = "TEXT")
    private String bank;

    @Column(name = "contact_notes", columnDefinition = "TEXT")
    private String contactNotes;

    @Column(name = "phone", columnDefinition = "TEXT")
    private String phone;

    @Column(name = "profession", columnDefinition = "TEXT")
    private String profession;

    @Column(name = "birthdate", columnDefinition = "TEXT")
    private String birthDate;

    @Column(name = "bankaccount", columnDefinition = "TEXT")
    private String bankAccount;

    @Column(name = "phone_company", columnDefinition = "TEXT")
    private String phoneCompany;

    @Column(name = "phone_central", columnDefinition = "TEXT")
    private String phoneCentral;

    @Column(name = "fax", columnDefinition = "TEXT")
    private String fax;

    @Column(name = "email1", columnDefinition = "TEXT")
    private String email1;

    @Column(name = "email2", columnDefinition = "TEXT")
    private String email2;

    @Column(name = "company_is", columnDefinition = "TEXT")
    private String companyIs;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant  updatedAt;

    @Column(name = "deleted_at")
    private Instant  deletedAt;
}
