/*
 * Copyright (c) 2025 ProjeXion. All rights reserved.
 */
package com.projexion.api.Project;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
public class ProjectService {
    /**
     * Server side pagination on element list
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @WithSession
    protected Uni<Map<String, Object>> findAllByPage(int pageIndex, int pageSize) {
        return ProjectEntity.count()                  // first get total count
                .flatMap(totalCount ->
                        ProjectEntity.find("ORDER BY id DESC")            // then get paginated data
                                .page(pageIndex, pageSize)
                                .list()
                                .map(list -> {
                                    Map<String, Object> response = new HashMap<>();
                                    response.put("data", list);
                                    response.put("totalCount", totalCount);
                                    return response;
                                })
                );
    }


    public Uni<Map<String, Long>> getProjectName() {
        Map<String, Long> projectTitle = new HashMap<>();
        return ProjectEntity.<ProjectEntity>listAll()
                .onItem().transformToUni(projectEntities -> {
                    projectEntities.stream()
                            .peek(entity -> projectTitle.put(entity.getTitle(), entity.getId()))
                            .map(ProjectEntity::getTitle)
                            .collect(Collectors.toList());
                    return Uni.createFrom().item(projectTitle);
                });
    }

    /**
     * Find Project
     * @param id
     * @return
     */
    public Uni<ProjectEntity> findItemById(Long id) {
        return ProjectEntity.findById(id);
    }

    /**
     * Create Project
     * @param project
     * @return
     */
    @WithTransaction
    public Uni<ProjectEntity> createProject(ProjectEntity project) {
        return Panache.withTransaction(project::persist).replaceWith(project);
    }

    /**
     * Update Project
     * @param id
     * @param updatedProject
     * @return
     */
    public Uni<Response> updateProject(Long id, ProjectEntity updatedProject) {
        return Panache.withTransaction(() ->
                ProjectEntity.<ProjectEntity>findById(id)
                        .onItem().ifNotNull().transformToUni(existing -> {
                            // Copy all updatable fields
                            existing.setTitle(updatedProject.getTitle());
                            existing.setProductionYear(updatedProject.getProductionYear());
                            existing.setCountry(updatedProject.getCountry());
                            existing.setAka(updatedProject.getAka());
                            existing.setOrigVers(updatedProject.getOrigVers());
                            existing.setVersInfo(updatedProject.getVersInfo());
                            existing.setProduction(updatedProject.getProduction());
                            existing.setRegie(updatedProject.getRegie());
                            existing.setIncharge(updatedProject.getIncharge());
                            existing.setInchargeSec(updatedProject.getInchargeSec());
                            existing.setBookAuthor(updatedProject.getBookAuthor());
                            existing.setCoAuthor(updatedProject.getCoAuthor());
                            existing.setPremiereDate(updatedProject.getPremiereDate());
                            existing.setCinemaStartDate(updatedProject.getCinemaStartDate());
                            existing.setTvStartDate(updatedProject.getTvStartDate());
                            existing.setSynopsisDe(updatedProject.getSynopsisDe());
                            existing.setFestivalInfo(updatedProject.getFestivalInfo());
                            existing.setAwards(updatedProject.getAwards());
                            existing.setStatus(updatedProject.getStatus());
                            existing.setMinutes(updatedProject.getMinutes());
                            existing.setMeters(updatedProject.getMeters());
                            existing.setFormats(updatedProject.getFormats());
                            existing.setMovieId(updatedProject.getMovieId());
                            existing.setVodLink(updatedProject.getVodLink());
                            existing.setScreenerLink(updatedProject.getScreenerLink());
                            existing.setTrailerLink(updatedProject.getTrailerLink());
                            existing.setWebKeywords(updatedProject.getWebKeywords());
                            existing.setIsan(updatedProject.getIsan());
                            existing.setImdb(updatedProject.getImdb());
                            existing.setEingabenFoerderer(updatedProject.getEingabenFoerderer());
                            existing.setKopien(updatedProject.getKopien());
                            existing.setTonstudio(updatedProject.getTonstudio());
                            existing.setMischung(updatedProject.getMischung());
                            existing.setWeitereTonbearbeitung(updatedProject.getWeitereTonbearbeitung());
                            existing.setVideotechnik(updatedProject.getVideotechnik());
                            existing.setSchnittassi(updatedProject.getSchnittassi());
                            existing.setSchnitt(updatedProject.getSchnitt());
                            existing.setWeitereBildbearbeitung(updatedProject.getWeitereBildbearbeitung());
                            existing.setStereodolby(updatedProject.getStereodolby());
                            existing.setFormatDreh(updatedProject.getFormatDreh());
                            existing.setFormatSchnitt(updatedProject.getFormatSchnitt());
                            existing.setAuswertung(updatedProject.getAuswertung());
                            existing.setFilmformat(updatedProject.getFilmformat());
                            existing.setBildformat(updatedProject.getBildformat());
                            existing.setLabor(updatedProject.getLabor());
                            existing.setTonsystem(updatedProject.getTonsystem());
                            existing.setNegmont(updatedProject.getNegmont());

                            // Audit fields
                            existing.setUpdatedAt(Instant.now());

                            // Persist updated entity
                            return existing.persist()
                                    .replaceWith(Response.ok(existing).build());
                        })
                        .onItem().ifNull().continueWith(Response.status(NOT_FOUND).build())
        );
    }

    /**
     * Delete Project
     * @param id
     * @return
     */
    public Uni<Response> deleteProject(Long id) {
        return Panache.withTransaction(() -> ProjectEntity.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(NO_CONTENT).build() : Response.ok().status(NOT_FOUND).build());
    }
}
