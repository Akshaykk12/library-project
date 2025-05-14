package com.capgemini.library_project.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;

@Service
public class AuthorServicesImpl implements AuthorServices{
		private AuthorRepository authorRepository;
		private String UPLOAD_DIR = "uploads/";
	
	@Autowired
	public AuthorServicesImpl(AuthorRepository authorRepository) {
		// TODO Auto-generated constructor stub
		this.authorRepository =authorRepository;
	}

	@Override
	public Author createAuthor(Author a) {
		// TODO Auto-generated method stub
		return authorRepository.save(a);
	}

	@Override
	public List<Author> findAllAuthors() {
		// TODO Auto-generated method stub
		return authorRepository.findAll();
	}

	@Override
	public Author updateAuthorById( Long id ,Author a) {
		// TODO Auto-generated method stub
		Author author = authorRepository.findById(id).orElseThrow(()->new RuntimeException("Author with this id not found"+id));
		author.setAuthorName(a.getAuthorName());
		author.setAuthorBio(a.getAuthorBio());
		author.setAuthorSocial(a.getAuthorSocial());
		return authorRepository.save(author);
	}

	@Override
	public Author findAuthorById(Long id) {
		// TODO Auto-generated method stub
		return authorRepository.findById(id).orElseThrow(()->new RuntimeException("Author with this id not found"+id));
	}

	@Override
	public Boolean deleteAuthorById(Long id) {
		// TODO Auto-generated method stub
		Author author = authorRepository.findById(id).orElseThrow( ()->new RuntimeException("Author with this id not found"+id));
		authorRepository.delete(author);
		return true;
	}
	
	@Override
	public Author updateImage(Long authorId, MultipartFile image) throws IOException {
	    // Ensure the upload directory exists
	    Files.createDirectories(Paths.get(UPLOAD_DIR));

	    // Generate a unique file name
	    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	    Path filePath = Paths.get(UPLOAD_DIR, fileName);

	    // Save the uploaded image file
	    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    // Find the existing user
	    Author author = authorRepository.findById(authorId)
	                   .orElseThrow(() -> new RuntimeException("Author with id " + authorId + " not found"));

	    // Update the Author image path
	    author.setAuthorImage(fileName);

	    // Save and return the updated author
	    return authorRepository.save(author);
	}

	@Override
	public Author getImage(Long authorid) {
		return authorRepository.findById(authorid).orElseThrow(() -> new RuntimeException());
	}

}
