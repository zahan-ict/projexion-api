/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.Project;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "production_year")
    private String productionYear;

    @Column(name = "country")
    private String country;

    @Column(name = "aka", columnDefinition = "TEXT")
    private String aka;

    @Column(name = "orig_vers")
    private String origVers;

    @Column(name = "vers_info")
    private String versInfo;

    @Column(name = "production", columnDefinition = "TEXT")
    private String production;

    @Column(name = "regie", columnDefinition = "TEXT")
    private String regie;

    @Column(name = "incharge")
    private String incharge;

    @Column(name = "incharge_sec")
    private String inchargeSec;

    @Column(name = "book_author")
    private String bookAuthor;

    @Column(name = "co_author")
    private String coAuthor;

    @Column(name = "premiere_date")
    private String premiereDate;

    @Column(name = "cinema_start_date")
    private String cinemaStartDate;

    @Column(name = "tv_start_date")
    private String tvStartDate;

    @Column(name = "synopsis_de", columnDefinition = "TEXT")
    private String synopsisDe;

    @Column(name = "festival_info", columnDefinition = "TEXT")
    private String festivalInfo;

    @Column(name = "awards", columnDefinition = "TEXT")
    private String awards;

    @Column(name = "status")
    private String status;

    @Column(name = "minutes")
    private String minutes;

    @Column(name = "meters")
    private String meters;

    @Column(name = "formats")
    private String formats;

    @Column(name = "movieid")
    private String movieId;

    @Column(name = "vod_link")
    private String vodLink;

    @Column(name = "screener_link")
    private String screenerLink;

    @Column(name = "trailer_link")
    private String trailerLink;

    @Column(name = "web_keywords")
    private String webKeywords;

    @Column(name = "isan")
    private String isan;

    @Column(name = "imdb")
    private String imdb;

    @Column(name = "eingaben_foerderer", columnDefinition = "TEXT")
    private String eingabenFoerderer;

    @Column(name = "kopien", columnDefinition = "TEXT")
    private String kopien;

    @Column(name = "tonstudio", columnDefinition = "TEXT")
    private String tonstudio;

    @Column(name = "mischung")
    private String mischung;

    @Column(name = "weitere_tonbearbeitung", columnDefinition = "TEXT")
    private String weitereTonbearbeitung;

    @Column(name = "videotechnik")
    private String videotechnik;

    @Column(name = "schnittassi")
    private String schnittassi;

    @Column(name = "schnitt")
    private String schnitt;

    @Column(name = "weitere_bildbearbeitung", columnDefinition = "TEXT")
    private String weitereBildbearbeitung;

    @Column(name = "stereodolby")
    private String stereodolby;

    @Column(name = "format_dreh", columnDefinition = "TEXT")
    private String formatDreh;

    @Column(name = "format_schnitt", columnDefinition = "TEXT")
    private String formatSchnitt;

    @Column(name = "auswertung", columnDefinition = "TEXT")
    private String auswertung;

    @Column(name = "filmformat")
    private String filmformat;

    @Column(name = "bildformat")
    private String bildformat;

    @Column(name = "labor", columnDefinition = "TEXT")
    private String labor;

    @Column(name = "tonsystem")
    private String tonsystem;

    @Column(name = "negmont", columnDefinition = "TEXT")
    private String negmont;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant deletedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
