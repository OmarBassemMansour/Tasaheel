package Graduation.Project.Tasaheel.models;

public class UserDTO {

	private Long id;

	private String username;
	private String password;
	private String email;
	private String role;
	private String phoneNb;
	private String gender;
	private String nationalId;
	private String country;
	private String city;

	public UserDTO() {
	}

	public UserDTO(Long id, String username, String email ,  String phoneNb, String gender, String nationalId, String country, String city) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.phoneNb = phoneNb;
		this.gender = gender;
		this.nationalId = nationalId;
		this.country = country;
		this.city = city;
	}
	public String getPhoneNb() {
		return phoneNb;
	}

	public void setPhoneNb(String phoneNb) {
		this.phoneNb = phoneNb;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}