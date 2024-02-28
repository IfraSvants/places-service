package com.placesservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.placesservice.models.ImagePlaceEntity;

@Repository
public interface ImageDao extends JpaRepository<ImagePlaceEntity, Integer> {

}
