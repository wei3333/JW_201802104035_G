package domain;
import java.io.Serializable;

public final class Teacher implements Comparable<Teacher>,Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String no;
	private String name;
	private ProfTitle profTitle;
	private Degree degree;
	private Department department;

	public Teacher(Integer id,
				   String no,
				   String name,
				   ProfTitle title,
				   Degree degree,
                   Department department) {
		this(no,name, title, degree, department);
		this.id = id;

	}
	public Teacher(String no, String name, ProfTitle title, Degree degree, Department department) {
		super();
		this.no = no;
		this.name = name;
		this.profTitle = title;
		this.degree = degree;
		this.department = department;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNo(){
		return no;
	}

	public void setNo(){
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProfTitle getTitle() {
		return this.profTitle;
	}

	public void setTitle(ProfTitle title) {
		this.profTitle = title;
	}

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	




	@Override
	public int compareTo(Teacher o) {
		// TODO Auto-generated method stub
		return this.id-o.getId();
	}

	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "Teacher ( "
	        + super.toString() + TAB
	        + "id = " + this.id + TAB
	        + "name = " + this.name + TAB
	        + "title = " + this.profTitle + TAB
	        + "degree = " + this.degree + TAB
	        + "department = " + this.department + TAB
	        + " )";
	
	    return retValue;
	}

}
