package com.placesservice.service.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.placesservice.dao.ImageDao;
import com.placesservice.dao.PlaceDao;
import com.placesservice.dto.PlaceDto;
import com.placesservice.exception.EntityNotFoundException;
import com.placesservice.models.ImagePlaceEntity;
import com.placesservice.models.PlacesEntity;
import com.placesservice.service.facade.PlaceService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private PlaceDao placeDao;
	private ModelMapper modelMapper;
	private ImageDao imageDao;
	
	@Override
	public List<PlaceDto> findAll() {
		return placeDao
				.findAll()
				.stream().map( el->modelMapper.map(el, PlaceDto.class) )
				.collect(Collectors.toList())
				;
	}

	@Override
	public PlaceDto save(PlaceDto placeDto) {
		
		System.out.println("tesssssssssssssst : "+modelMapper.map(placeDto, PlacesEntity.class));
		PlacesEntity entity = modelMapper.map(placeDto, PlacesEntity.class);
		
		List<ImagePlaceEntity> images = entity.getImages();
		entity.setImages(null);
		PlacesEntity placesEntity = placeDao.save(entity);
		
		List<ImagePlaceEntity> imageList = new ArrayList<>(); 
		for( ImagePlaceEntity image : images ) {
			
			image.setPlace(placesEntity);
			ImagePlaceEntity saved = imageDao.save(image);
			imageList.add(saved);
			
		}
		placesEntity.setImages(imageList);
		
		return modelMapper.map(placesEntity, PlaceDto.class);
	}

	@Override
	public PlaceDto update(PlaceDto placeDto, Integer id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlaceDto findById(Integer id) {
		PlacesEntity placesEntity = placeDao.findById(id).orElseThrow( ()->new EntityNotFoundException("Place Not Found"));
		return modelMapper.map(placesEntity , PlaceDto.class);
	}

	@Override
	public List<PlaceDto> findByVille(String ville) {
		return placeDao
			.findByVille(ville)
			.stream().map( el->modelMapper.map(el, PlaceDto.class) )
			.collect(Collectors.toList())
			;
	}

	@Override
	public void delete(Integer id) {
		
		PlacesEntity placesEntity = placeDao.findById(id).orElseThrow( ()->new EntityNotFoundException("Place Not Found"));
		placeDao.delete(placesEntity);
		
	}

}
