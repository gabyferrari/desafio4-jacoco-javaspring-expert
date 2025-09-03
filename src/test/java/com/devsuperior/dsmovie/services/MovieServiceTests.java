package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;
	
	@Mock
	private MovieRepository movieRepository;
	
	private long existingId, nonExistId, dependentId;
	private String movieName;
	private PageImpl<MovieEntity> page;
	private MovieDTO movieDto;
	private MovieEntity movie;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistId = 2L;
		dependentId = 3L;
		movieName = "Test Movie";
		movie = MovieFactory.createMovieEntity();
		page = new PageImpl<>(List.of(movie));
		movieDto = MovieFactory.createMovieDTO();
		
		Mockito.when(movieRepository.searchByTitle(any(), (Pageable)any())).thenReturn(page);
		
		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistId)).thenReturn(Optional.empty());
		
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		
		Mockito.when(movieRepository.getReferenceById(existingId)).thenReturn(movie);
		Mockito.when(movieRepository.getReferenceById(nonExistId)).thenThrow(EntityNotFoundException.class);
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<MovieDTO> result = service.findAll(movieName, pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getTitle(), movieName);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getTitle(), movie.getTitle());
		
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO result = service.insert(movieDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movie.getId());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.update(existingId, movieDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getTitle(), movieDto.getTitle());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			MovieDTO result = service.update(nonExistId, movieDto);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
