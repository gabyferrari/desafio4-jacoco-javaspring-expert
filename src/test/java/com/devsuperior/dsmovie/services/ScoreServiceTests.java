package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	private long existingId, nonExistId;
	private MovieEntity movie;
	private ScoreEntity score;
	private UserEntity user;
	private ScoreDTO scoreDto;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistId = 2L;
		score = ScoreFactory.createScoreEntity();
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		movie.getScores().add(score);
		scoreDto = ScoreFactory.createScoreDTO();
		
		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistId)).thenReturn(Optional.empty());
		
		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);
		
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		MovieDTO result = service.saveScore(scoreDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getTitle(), movie.getTitle());
		Assertions.assertEquals(result.getScore(), movie.getScore());
		Assertions.assertEquals(result.getCount(), movie.getCount());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.when(userService.authenticated()).thenReturn(user);
		
	    ScoreDTO invalidScoreDto = new ScoreDTO(nonExistId, ScoreFactory.scoreValue);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			MovieDTO result = service.saveScore(invalidScoreDto);
		});
	}
}
