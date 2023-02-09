package com.example.demo;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	ApplicationListener<ApplicationReadyEvent> personListener(PersonService service) {
		return event -> {
			Person createdPerson = service.create("Test1", null);
			Person second = service.create("Test2", createdPerson);

			service.check(createdPerson, second.getId());
		};
	}
}

@Getter
@Setter

@Entity
@AllArgsConstructor
@NoArgsConstructor
class Person {
	@Id
	@GeneratedValue
	private Long id; private String name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id")
	private Person person;

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			System.out.println("Class of input arg: " + o.getClass());
		}

		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Person other = (Person) o;
		return id != null && Objects.equals(id, other.id);
	}

	public boolean equalsByGetters(Object o) {
		if (o != null) {
			System.out.println("Class of input arg: " + o.getClass());
		}
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Person other = (Person) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}
}

interface PersonRepository extends JpaRepository<Person, Long> {
}

@Service
@RequiredArgsConstructor
class PersonService {
	private final PersonRepository repository;

	@Transactional
	public Person create(String name, Person other) {
		Person person = new Person();
		person.setName(name);
		person.setPerson(other);
		repository.save(person);
		person.setName("Vova");

		return person;
	}

	@Transactional(readOnly = true)
	public void check(Person person, Long other) {
		Person otherPerson = repository.findById(other).orElseThrow(() -> new RuntimeException("Person not found"));

		System.out.println("Test for equals with direct field access: " + person.equals(otherPerson.getPerson()));
		System.out.println("Test for equals with getters: " + person.equalsByGetters(otherPerson.getPerson()));

	}

	public Person get(Long id) {
		return repository.findById(id).orElse(null);
	}
}
