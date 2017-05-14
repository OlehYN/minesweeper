import java.io.Serializable;

public class Top implements Serializable {

	private static final long serialVersionUID = -2273618764808310264L;
	private Person[] persons;// top players
	private final int QUANTITY = 10;

	public Top() {
		persons = new Person[QUANTITY + 1];
	}

	/**
	 * Checks the new person and forms new top
	 * 
	 * @param person
	 *            Person that possibly will be in the top
	 */
	public void addPerson(Person person) {
		persons[QUANTITY] = new Person(person.getName(), person.getTime());
		Sorter.sort(persons);

	}

	@Override
	public String toString() {
		String res = "";

		for (int i = 0; i < persons.length - 1; i++) {
			if (persons[i] == null)
				res += (i + 1) + ". -------\n";
			else
				res += (i + 1) + ". " + persons[i].getName() + ": " + persons[i].getTime() + "\n";
		}
		return res;
	}

}
