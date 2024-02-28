package com.placesservice.service.facade;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.placesservice.dto.PlaceDto;

public interface PlaceService {

	List<PlaceDto> findAll();
	
	PlaceDto save( PlaceDto placeDto );
	
	PlaceDto update( PlaceDto placeDto , Integer id ) throws NotFoundException;
	
	PlaceDto findById( Integer id );
	
	List<PlaceDto> findByVille( String ville );
	
	void delete( Integer id );
	
}
