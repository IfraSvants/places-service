package com.placesservice.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.placesservice.dto.ImagePlaceDto;
import com.placesservice.dto.PlaceDto;
import com.placesservice.service.facade.PlaceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/places")
public class PlaceController {
	
	final static Logger log = LogManager.getLogger(PlaceController.class);
	
	@Autowired
	private PlaceService placeService;
	
	@Value("${upload.dir}") 
    private String uploadDir;
	
	@GetMapping("")
	public ResponseEntity<List<PlaceDto>> findAll(){
			return new ResponseEntity<>(placeService.findAll(), HttpStatus.OK);
	}
	
	@PostMapping( value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<PlaceDto> save( 
			@Valid @RequestParam("name")  String name,
			@RequestParam("description")  String description,
			@RequestParam("latitude")  String latitude,
			@RequestParam("longitude")  String longitude,
			@RequestParam("ville")  String ville,
			@RequestPart("imageFiles") List<MultipartFile> imageFiles
			)throws IOException{
		PlaceDto placeDto = new PlaceDto(null, name, description, Float.parseFloat(latitude), Float.parseFloat(longitude), ville, null);
		
		if(imageFiles == null || imageFiles.isEmpty()) {
			
			log.warn("Please select a file to upload");
			return ResponseEntity.accepted().body(placeDto);
			
		}
		
		List<ImagePlaceDto> images = new ArrayList<>();
		
		for (MultipartFile imageFile : imageFiles) {
			
			ImagePlaceDto image = new ImagePlaceDto();
			String fileId = UUID.randomUUID().toString().substring(0, 8);
			String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
			// Trouvez l'index du dernier point dans le nom du fichier
	        int lastIndex = filename.lastIndexOf('.');
	        
	        // Extraire le nom de fichier sans l'extension
	        String fileNameWithoutExtension = lastIndex >= 0 ? filename.substring(0, lastIndex) : filename;

	        // Extraire l'extension du fichier
	        String fileExtension = lastIndex >= 0 && lastIndex < filename.length() - 1
	                ? filename.substring(lastIndex + 1)
	                : "";

			
			// Ajoutez l'identifiant au nom du fichier
		    String newFileName = fileNameWithoutExtension + "-" + fileId + "."+fileExtension;
		    
		    Path pathFile = Paths.get( uploadDir , newFileName);
			try {
				
				if (imageFile.isEmpty()) {
			        log.error("Failed to store empty file " + filename);
			        return ResponseEntity.accepted().body(placeDto);
			    }
				if (filename.contains("..")) {
			        // This is a security check
			        log.error("Cannot store file with relative path outside current directory " + filename);
			        return ResponseEntity.accepted().body(placeDto);
			    }
				try (InputStream inputStream = imageFile.getInputStream()) {
			        
			        // save file to target(server)
			        Files.copy(inputStream, pathFile, StandardCopyOption.REPLACE_EXISTING);

			        // read file to bytes
			        // Files.readAllBytes(pathFile);
			        image.setPath(newFileName);
			        
			        images.add(image);
			        
			        log.info("You successfully uploaded '" + filename + "'");
			        
			      }
				
			} catch (IOException e) {
				log.error("Failed to store file " + filename, e);
			}
	        
		}
		
		placeDto.setImages(images);
		PlaceDto saved = placeService.save(placeDto);
		
		return ResponseEntity.accepted().body(saved);
	}
	
	@PutMapping("/id/{id}")
	public ResponseEntity<PlaceDto> update(@Valid @RequestBody PlaceDto userDto, @PathVariable Integer id) throws NotFoundException {
		
//		UserDto dto = userService.findById(id);
//		userDto.setEmail(dto.getEmail());
//		userDto.setPassword(dto.getPassword());
//		
//		System.out.println("passsssssssssssssssword :"+dto.getPassword());
//		
//		UserDto updated = userService.update(userDto, id);
//		return ResponseEntity.accepted().body(updated);
		return null;
		
	}
	
	@GetMapping("/id/{id}")
    public ResponseEntity<PlaceDto> findById( @PathVariable("id") Integer id) {
		PlaceDto placeDto = placeService.findById(id);
    	return ResponseEntity.ok(placeDto);
    }
	
	@GetMapping("/ville/{ville}")
    public ResponseEntity<List<PlaceDto>> findByVille( @PathVariable("ville") String ville) {
		List<PlaceDto> list = placeService.findByVille(ville);
    	return ResponseEntity.ok(list);
    }
	
	@DeleteMapping("/id/{id}")
	public ResponseEntity<?> delete( @PathVariable("id") Integer id) {
		placeService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
