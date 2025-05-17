package com.capgemini.library_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendingBookForUserDto {

	 	private Long bookId;
	 	 private String authorName;
	    private String title;
	    private String category;
	    private Long availableCopies;
	    private Long issueCount;
}
