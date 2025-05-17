package com.capgemini.library_project.dto;


import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDto {
	private Integer authorCount;
	private Integer bookCount;
	private Integer userCount;
	private Integer issueCount;
	private Integer overdueCount;
	private Map<String ,Long> categoryCount;
	private List<TrendingBookForUserDto> topBooksCount;
}