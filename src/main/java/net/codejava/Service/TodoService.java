package net.codejava.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.codejava.Todo;
import net.codejava.Repository.TodoRepository;

@Service
public class TodoService {

	@Autowired
	private TodoRepository todoRepository;

	public List<Todo> listAll(String keyword) {
		if (keyword != null) {
			return todoRepository.search(keyword);
		}
		return todoRepository.findAll();
	}

	public void save(Todo todo) {
		todoRepository.save(todo);
	}

	public Todo get(Long id) {
		return todoRepository.findById(id).get();
	}

	public void delete(Long id) {
		todoRepository.deleteById(id);
	}

}
