### Test
 Sorry, didn't have time to normal test, it is a bit strange, but emulates the behavior of the case that I had in my code. May be it is my fault and I will research the proxy creation deeply.
 
## Method where the output is defined
```	
    public void check(Person person, Long other) {
		Person otherPerson = repository.findById(other).orElseThrow(() -> new RuntimeException("Person not found"));

		System.out.println("Test for equals with direct field access: " + person.equals(otherPerson.getPerson()));
		System.out.println("Test for equals with getters: " + person.equalsByGetters(otherPerson.getPerson()));

	}
```

## Output
```
Class of input arg: class com.example.demo.Person$HibernateProxy$dHBofkAe
Test for equals with direct field access: false
Class of input arg: class com.example.demo.Person$HibernateProxy$dHBofkAe
Test for equals with getters: true
```
