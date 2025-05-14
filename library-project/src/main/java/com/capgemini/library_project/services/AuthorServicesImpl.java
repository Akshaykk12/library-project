package com.capgemini.library_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;

@Service
public class AuthorServicesImpl implements AuthorServices{
		private AuthorRepository authorRepository;
	
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

}
