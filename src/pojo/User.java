package pojo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class User {
	private int id;
	private String username;
	private String password;
	private String truename;
	private Byte isOnline;
	private byte isUseful;
	private String workshop;
	private String storehouse;
	private String identity;

	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "username", nullable = false, length = 25)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Basic
	@Column(name = "password", nullable = false, length = 64)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Basic
	@Column(name = "truename", nullable = false, length = 10)
	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	@Basic
	@Column(name = "isOnline", nullable = true)
	public Byte getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Byte isOnline) {
		this.isOnline = isOnline;
	}

	@Basic
	@Column(name = "isUseful", nullable = false)
	public byte getIsUseful() {
		return isUseful;
	}

	public void setIsUseful(byte isUseful) {
		this.isUseful = isUseful;
	}

	@Basic
	@Column(name = "workshop", nullable = false, length = 12)
	public String getWorkshop() {
		return workshop;
	}

	public void setWorkshop(String workshop) {
		this.workshop = workshop;
	}

	@Basic
	@Column(name = "storehouse", nullable = false, length = 10)
	public String getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(String storehouse) {
		this.storehouse = storehouse;
	}

	@Basic
	@Column(name = "identity", nullable = false, length = 11)
	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return id == user.id &&
				isUseful == user.isUseful &&
				Objects.equals(username, user.username) &&
				Objects.equals(password, user.password) &&
				Objects.equals(truename, user.truename) &&
				Objects.equals(isOnline, user.isOnline) &&
				Objects.equals(workshop, user.workshop) &&
				Objects.equals(storehouse, user.storehouse) &&
				Objects.equals(identity, user.identity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, truename, isOnline, isUseful, workshop, storehouse, identity);
	}
}
