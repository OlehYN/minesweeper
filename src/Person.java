import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = -2891391577195348031L;
	private String name;
	private int time;

	Person(String name, int time) {
		this.setName(name);
		this.setTime(time);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
