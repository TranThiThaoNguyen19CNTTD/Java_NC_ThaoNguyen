package net.codejava.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.codejava.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	@Query("SELECT p FROM Todo p WHERE " + "CONCAT (p.id, p.tieude)" + " LIKE %?1%")
	public List<Todo> search(String keyword);

}
