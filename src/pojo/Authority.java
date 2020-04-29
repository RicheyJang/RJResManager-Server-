package pojo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Authority {
	private int id;
	private String status;
	private int teacher;
	private int header;
	private int admin;
	private int keeper;
	private int accountant;

	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "status", nullable = false, length = 10)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Basic
	@Column(name = "teacher", nullable = false)
	public int getTeacher() {
		return teacher;
	}

	public void setTeacher(int teacher) {
		this.teacher = teacher;
	}

	@Basic
	@Column(name = "header", nullable = false)
	public int getHeader() {
		return header;
	}

	public void setHeader(int header) {
		this.header = header;
	}

	@Basic
	@Column(name = "admin", nullable = false)
	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	@Basic
	@Column(name = "keeper", nullable = false)
	public int getKeeper() {
		return keeper;
	}

	public void setKeeper(int keeper) {
		this.keeper = keeper;
	}

	@Basic
	@Column(name = "accountant", nullable = false)
	public int getAccountant() {
		return accountant;
	}

	public void setAccountant(int accountant) {
		this.accountant = accountant;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Authority authority = (Authority) o;
		return id == authority.id &&
				teacher == authority.teacher &&
				header == authority.header &&
				admin == authority.admin &&
				keeper == authority.keeper &&
				accountant == authority.accountant &&
				Objects.equals(status, authority.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, status, teacher, header, admin, keeper, accountant);
	}
}
