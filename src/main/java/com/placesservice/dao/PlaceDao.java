package com.placesservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.placesservice.models.PlacesEntity;
import java.util.List;


@Repository
public interface PlaceDao extends JpaRepository<PlacesEntity, Integer> {

	List<PlacesEntity> findByVille(String ville);
	
}
