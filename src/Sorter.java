import java.util.Arrays;
import java.util.Comparator;

public class Sorter {
	private static Comparator<Person> comparator = new Comparator<Person>() {

		public int compare(Person p1, Person p2) {

			if (p1 == null)
				return 1;
			if (p2 == null)
				return -1;
			else if (p2.getTime() < p1.getTime())
				return 1;
			else
				return -1;

		}
	};

	public static void sort(Person[] persons) {
		Arrays.sort(persons, comparator);
	}
}
